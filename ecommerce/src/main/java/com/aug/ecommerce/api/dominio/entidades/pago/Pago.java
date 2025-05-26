package com.aug.ecommerce.api.dominio.entidades.pago;

import java.util.Objects;
import java.util.UUID;

public class Pago {
    public enum Estado {
        PENDIENTE, CONFIRMADO, FALLIDO
    }

    private final UUID id;
    private final UUID ordenId;
    private final double monto;
    private final String metodo; // Ej: "tarjeta", "paypal", etc.
    private Estado estado;

    public Pago(UUID id, UUID ordenId, double monto, String metodo) {
        this.id = Objects.requireNonNull(id, "El id no puede ser nulo");
        this.ordenId = Objects.requireNonNull(ordenId, "El ordenId no puede ser nulo");

        if (monto <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a cero");

        this.monto = monto;
        this.metodo = Objects.requireNonNull(metodo, "El método no puede ser nulo");
        this.estado = Estado.PENDIENTE;
    }

    public UUID getId() { return id; }
    public UUID getOrdenId() { return ordenId; }
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
