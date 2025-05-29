package com.aug.ecommerce.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de aplicación encargado de coordinar la validación de órdenes.
 * Acumula los eventos de validación (cliente, producto, stock) hasta que se completen todas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrdenValidacionService {

    private final OrdenService ordenService;

    // Mapa concurrente que guarda el progreso de validación de cada orden
    private final Map<Long, ResultadoValidacion> validaciones = new ConcurrentHashMap<>();

    /**
     * Registra una validación exitosa para un tipo específico (CLIENTE, PRODUCTO, STOCK).
     * Si la orden ya tiene todas las validaciones, se marca como LISTA_PARA_PAGO.
     */
    public void registrarValidacionExitosa(Long ordenId, String tipo) {
        // Obtiene o crea un objeto ResultadoValidacion para esta orden
        ResultadoValidacion resultado = validaciones.computeIfAbsent(ordenId, ResultadoValidacion::new);

        // Marca la validación como exitosa
        resultado.marcarExitosa(tipo);

        // Si ya se cumplieron todas las validaciones requeridas
        if (resultado.isCompleta()) {
            ordenService.marcarOrdenValidada(ordenId);
            validaciones.remove(ordenId);
            log.info("Orden {} validada completamente", ordenId);
        } else {
            log.debug("Orden {} aún en validación: {}", ordenId, resultado);
        }
    }

    /**
     * Registra que una validación falló. Se descarta cualquier validación acumulada.
     * La orden pasa al estado VALIDACION_FALLIDA.
     */
    public void registrarValidacionFallida(Long ordenId, String tipo) {
        ordenService.marcarOrdenFallida(ordenId);
        validaciones.remove(ordenId);
        log.warn("Orden {} falló validación de tipo {}", ordenId, tipo);
    }
}
