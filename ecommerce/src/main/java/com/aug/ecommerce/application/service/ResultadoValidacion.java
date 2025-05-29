package com.aug.ecommerce.application.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Objeto auxiliar que representa el progreso de validación de una orden.
 * Acumula los tipos de validación que han sido exitosos.
 */
public class ResultadoValidacion {

    private final Long ordenId;

    // Tipos de validaciones que se han completado exitosamente
    private final Set<String> validacionesExitosas = new HashSet<>();

    // Tipos de validación requeridos para considerar una orden como validada
    private static final Set<String> VALIDACIONES_REQUERIDAS =
            Set.of("CLIENTE", "PRODUCTO", "STOCK");

    public ResultadoValidacion(Long ordenId) {
        this.ordenId = ordenId;
    }

    /**
     * Marca un tipo de validación como exitosa (por ejemplo, "CLIENTE").
     */
    public void marcarExitosa(String tipo) {
        validacionesExitosas.add(tipo);
    }

    /**
     * Indica si ya se completaron todas las validaciones requeridas.
     */
    public boolean isCompleta() {
        return validacionesExitosas.containsAll(VALIDACIONES_REQUERIDAS);
    }

    /**
     * Devuelve el conjunto de validaciones completadas (solo para depuración).
     */
    public Set<String> getValidacionesExitosas() {
        return Collections.unmodifiableSet(validacionesExitosas);
    }

    @Override
    public String toString() {
        return "ValidacionesCompletas=" + validacionesExitosas;
    }
}
