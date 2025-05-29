package com.aug.ecommerce.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdenValidacionService {

    private final OrdenService ordenService;

    private final Map<Long, Set<String>> validacionesPorOrden = new ConcurrentHashMap<>();

    private static final Set<String> VALIDACIONES_REQUERIDAS = Set.of("CLIENTE", "PRODUCTO", "STOCK");

    public void registrarValidacionExitosa(Long ordenId, String tipo) {
        validacionesPorOrden
                .computeIfAbsent(ordenId, k -> new HashSet<>())
                .add(tipo);

        log.debug("Validación exitosa registrada: orden={}, tipo={}", ordenId, tipo);

        if (validacionesPorOrden.get(ordenId).containsAll(VALIDACIONES_REQUERIDAS)) {
            ordenService.marcarOrdenValidada(ordenId);
            validacionesPorOrden.remove(ordenId);
            log.info("Orden {} validada completamente", ordenId);
        }
    }

    public void registrarValidacionFallida(Long ordenId, String tipo) {
        log.warn("Orden {} falló validación de tipo {}", ordenId, tipo);
        ordenService.marcarOrdenFallida(ordenId);
        validacionesPorOrden.remove(ordenId);
    }
}
