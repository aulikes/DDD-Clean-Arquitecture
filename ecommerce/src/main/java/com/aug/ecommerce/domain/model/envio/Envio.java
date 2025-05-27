package com.aug.ecommerce.domain.model.envio;

import java.util.Objects;
import java.util.UUID;

public class Envio {
    public enum Estado {
        PREPARANDO, DESPACHADO, ENTREGADO
    }

    private final Long id;
    private final Long ordenId;
    private final String direccionEnvio;
    private Estado estado;
    private String trackingNumber;

    public Envio(Long id, Long ordenId, String direccionEnvio) {
        this.id = id;
        this.ordenId = Objects.requireNonNull(ordenId, "La orden no puede ser nula");
        this.direccionEnvio = Objects.requireNonNull(direccionEnvio, "La dirección no puede ser nula");
        this.estado = Estado.PREPARANDO;
    }

    public Long getId() { return id; }
    public Long getOrdenId() { return ordenId; }
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
