package com.aug.ecommerce.domain.model.envio;

public enum EstadoEnvio {

    PENDIENTE("PENDIENTE"),
    PREPARANDO("PREPARANDO"),
    ENTREGADO("ENTREGADO"),
    DESPACHADO("DESPACHADO"),
    FALLIDO("FALLIDO");

    private final String value;

    private EstadoEnvio(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
