package com.aug.ecommerce.api.dominio.entidades.envio;

import java.util.Objects;
import java.util.UUID;

public class Envio {
    public enum Estado {
        PREPARANDO, DESPACHADO, ENTREGADO
    }

    private final UUID id;
    private final UUID ordenId;
    private final String direccionEnvio;
    private Estado estado;
    private String trackingNumber;

    public Envio(UUID id, UUID ordenId, String direccionEnvio) {
        this.id = Objects.requireNonNull(id, "El id no puede ser nulo");
        this.ordenId = Objects.requireNonNull(ordenId, "La orden no puede ser nula");
        this.direccionEnvio = Objects.requireNonNull(direccionEnvio, "La dirección no puede ser nula");
        this.estado = Estado.PREPARANDO;
    }

    public UUID getId() { return id; }
    public UUID getOrdenId() { return ordenId; }
    public String getDireccionEnvio() { return direccionEnvio; }
    public Estado getEstado() { return estado; }
    public String getTrackingNumber() { return trackingNumber; }

    public void despachar(String trackingNumber) {
        if (estado != Estado.PREPARANDO)
            throw new IllegalStateException("Solo puede despacharse un envío que está preparando");
        this.trackingNumber = Objects.requireNonNull(trackingNumber, "El número de seguimiento no puede ser nulo");
        this.estado = Estado.DESPACHADO;
    }

    public void entregar() {
        if (estado != Estado.DESPACHADO)
            throw new IllegalStateException("Solo se puede entregar un envío despachado");
        this.estado = Estado.ENTREGADO;
    }
}
