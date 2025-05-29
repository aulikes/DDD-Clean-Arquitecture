package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.listener.ValidacionCrearOrden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    // Tipos de validación requeridos
    private static final Set<ValidacionCrearOrden> VALIDACIONES_REQUERIDAS = Set.of(
            ValidacionCrearOrden.CLIENTE, ValidacionCrearOrden.PRODUCTO, ValidacionCrearOrden.STOCK
    );

    /**
     * Registra una validación exitosa para una orden.
     * Si todas las validaciones requeridas están completas, marca la orden como lista para pago.
     */
    public void registrarValidacionExitosa(Long ordenId, ValidacionCrearOrden tipo) {
        // Acumula el tipo de validación exitosa
        validacionesPorOrden
                .computeIfAbsent(ordenId, id -> ConcurrentHashMap.newKeySet())
                .add(tipo);

        // Si todas las validaciones han sido exitosas:
        if (validacionesPorOrden.get(ordenId).containsAll(VALIDACIONES_REQUERIDAS)) {
            // Cambia el estado de la orden aquí
            ordenService.marcarOrdenValidada(ordenId);
            log.debug("Orden {} validada completamente", ordenId);
            // Limpia el acumulador para esa orden
            validacionesPorOrden.remove(ordenId);
        }
    }

    /**
     * Registra una validación fallida para una orden.
     * Solo se marca una vez por orden, aunque lleguen múltiples eventos fallidos.
     */
    public void registrarValidacionFallida(Long ordenId, ValidacionCrearOrden tipo) {
        ordenService.marcarOrdenFallida(ordenId);
    }
}
