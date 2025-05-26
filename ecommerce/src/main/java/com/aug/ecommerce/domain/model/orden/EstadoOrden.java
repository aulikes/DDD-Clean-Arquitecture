package com.aug.ecommerce.domain.model.orden;

import java.util.*;

/**
 * Un Objeto de Valor con reglas de transición válidas entre estados.
 * Encapsula las transiciones permitidas y evita que el aggregate viole el flujo de negocio.
 */
public final class EstadoOrden {
    public static final EstadoOrden NUEVA     = new EstadoOrden("NUEVA", List.of("PAGADA", "CANCELADA"));
    public static final EstadoOrden PAGADA    = new EstadoOrden("PAGADA", List.of("ENVIADA", "CANCELADA"));
    public static final EstadoOrden ENVIADA   = new EstadoOrden("ENVIADA", List.of("ENTREGADA"));
    public static final EstadoOrden ENTREGADA = new EstadoOrden("ENTREGADA", List.of());
    public static final EstadoOrden CANCELADA = new EstadoOrden("CANCELADA", List.of());

    private static final Map<String, EstadoOrden> estados = Map.of(
            "NUEVA", NUEVA,
            "PAGADA", PAGADA,
            "ENVIADA", ENVIADA,
            "ENTREGADA", ENTREGADA,
            "CANCELADA", CANCELADA
    );

    private final String valor;
    private final List<String> transicionesValidas;

    private EstadoOrden(String valor, List<String> transicionesValidas) {
        this.valor = valor;
        this.transicionesValidas = transicionesValidas;
    }

    public static EstadoOrden desde(String valor) {
        EstadoOrden estado = estados.get(valor);
        if (estado == null) throw new IllegalArgumentException("Estado inválido: " + valor);
        return estado;
    }

    public String getValor() { return valor; }

    public void validarTransicionA(EstadoOrden nuevoEstado) {
        if (!transicionesValidas.contains(nuevoEstado.valor)) {
            throw new IllegalStateException("Transición inválida de " + valor + " a " + nuevoEstado.valor);
        }
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof EstadoOrden otro) && this.valor.equals(otro.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
