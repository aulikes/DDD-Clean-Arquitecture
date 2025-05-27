package com.aug.ecommerce.domain.model.pago;

import java.util.Objects;

public class Pago {
    public enum Estado {
        PENDIENTE, CONFIRMADO, FALLIDO
    }

    private final Long id;
    private final Long ordenId;
    private final double monto;
    private final String metodo; // Ej: "tarjeta", "paypal", etc.
    private Estado estado;

    public Pago(Long id, Long ordenId, double monto, String metodo) {
        this.id = id;
        this.ordenId = Objects.requireNonNull(ordenId, "El ordenId no puede ser nulo");

        if (monto <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a cero");

        this.monto = monto;
        this.metodo = Objects.requireNonNull(metodo, "El método no puede ser nulo");
        this.estado = Estado.PENDIENTE;
    }

    public Long getId() { return id; }
    public Long getOrdenId() { return ordenId; }
    public double getMonto() { return monto; }
    public String getMetodo() { return metodo; }
    public Estado getEstado() { return estado; }

    public void confirmar() {
        if (estado != Estado.PENDIENTE)
            throw new IllegalStateException("El pago ya fue procesado");
        this.estado = Estado.CONFIRMADO;
    }

    public void fallar() {
        if (estado != Estado.PENDIENTE)
            throw new IllegalStateException("El pago ya fue procesado");
        this.estado = Estado.FALLIDO;
    }
}
