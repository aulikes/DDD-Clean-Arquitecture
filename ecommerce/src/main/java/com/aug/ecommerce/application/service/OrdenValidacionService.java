package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.event.EnvioPreparadoEvent;
import com.aug.ecommerce.application.event.PagoConfirmadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Servicio de aplicación encargado de coordinar la validación de órdenes.
 * Recibe eventos de validación (cliente, productos, stock) y decide el estado final.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrdenValidacionService {

    private final OrdenService ordenService;

    // Acumulador temporal en memoria (NO usar para varios pods/microservicios)
    private final Map<Long, Set<ValidacionCrearOrden>> validacionesPorOrden = new ConcurrentHashMap<>();
    // Conjunto de validaciones que deben completarse para que una orden sea válida
    private static final Set<ValidacionCrearOrden> VALIDACIONES_REQUERIDAS = Set.of(
            ValidacionCrearOrden.CLIENTE, ValidacionCrearOrden.PRODUCTO, ValidacionCrearOrden.STOCK
    );
    // Mapa de timeouts programados por orden
    private final Map<Long, ScheduledFuture<?>> timeouts = new ConcurrentHashMap<>();
    // Ejecutor para programar tareas con retraso (timeouts)
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private static final long TIMEOUT_SECONDS = 180;

    /**
     * Registra una validación exitosa para una orden.
     * Si todas las validaciones requeridas están completas, marca la orden como lista para pago.
     */
    public void registrarValidacionExitosa(Long ordenId, ValidacionCrearOrden tipo) {
        // Acumula el tipo de validación exitosa
        validacionesPorOrden
                .computeIfAbsent(ordenId, id -> {
                    log.debug("Iniciando acumulación de validaciones para orden {}", ordenId);
                    // Si es la PRIMERA validación, programa el timeout
                    programarTimeout(ordenId);
                    return ConcurrentHashMap.newKeySet();
                })
                .add(tipo);

        // Si todas las validaciones han sido exitosas:
        if (validacionesPorOrden.get(ordenId).containsAll(VALIDACIONES_REQUERIDAS)) {
            // Cambia el estado de la orden aquí
            ordenService.marcarOrdenValidada(ordenId);
            log.debug("Orden {} validada completamente", ordenId);
            // Limpia el acumulador para esa orden
            validacionesPorOrden.remove(ordenId);
            limpiarEstado(ordenId);
        }
    }
    /**
     * Registra una validación fallida. Cancela la espera de otras validaciones y
     * cambia el estado de la orden a fallida.
     */
    public void registrarValidacionFallida(Long ordenId, ValidacionCrearOrden tipo) {
        log.warn("Validación fallida [{}] en orden {}", tipo, ordenId);
        ordenService.marcarOrdenFallida(ordenId, "Validación fallida tipo: " + tipo.name());
        limpiarEstado(ordenId);
    }

    public void gestionarInformacionPago(PagoConfirmadoEvent event){
        log.warn("Gestionar Pago [{}] en orden {}", event.pagoId(), event.ordenId());
        if (event.exitoso()){
            ordenService.pagoConfirmado(event.ordenId());
        }
        else{
            ordenService.pagoRechazado(event.ordenId());
        }
    }

    public void gestionarInformacionEnvio(EnvioPreparadoEvent event){
        log.warn("Gestionar Envio [{}] en orden {}", event.envioId(), event.ordenId());
        if (event.exitoso()){
            ordenService.envioConfirmado(event.ordenId());
        }
        else{
            ordenService.envioError(event.ordenId());
        }
    }

    /**
     * Programa un timeout para la orden, que se ejecutará si no se completan
     * todas las validaciones dentro del tiempo configurado.
     */
    private void programarTimeout(Long ordenId) {
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            Set<ValidacionCrearOrden> acumuladas = validacionesPorOrden.getOrDefault(ordenId, Set.of());

            if (!acumuladas.containsAll(VALIDACIONES_REQUERIDAS)) {
                log.warn("Timeout de validaciones alcanzado para orden {}. Validaciones recibidas: {}", ordenId, acumuladas);
                ordenService.marcarOrdenFallida(ordenId,
                        "Timeout de validaciones alcanzado. Validaciones recibidas: " + acumuladas.toString());
                limpiarEstado(ordenId);
            }
        }, TIMEOUT_SECONDS, TimeUnit.SECONDS);
        timeouts.put(ordenId, future);
    }

    /**
     * Limpia los registros temporales asociados a la orden: validaciones y timeout.
     */
    private void limpiarEstado(Long ordenId) {
        validacionesPorOrden.remove(ordenId);
        Optional.ofNullable(timeouts.remove(ordenId))
                .ifPresent(f -> f.cancel(false));
        log.info("Limpieza completada para orden {}", ordenId);
    }
}
