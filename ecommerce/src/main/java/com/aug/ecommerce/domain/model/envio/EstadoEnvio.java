package com.aug.ecommerce.domain.model.envio;

public enum EstadoEnvio {

    PENDIENTE("PENDIENTE"),
    PREPARANDO("PREPARANDO"),
    ENVIADO("ENVIADO"),  // es actualizado por webhooks
    ENTREGADO("ENTREGADO"), // es actualizado por webhooks
    DESPACHADO("DESPACHADO"), // es actualizado por webhooks
    FALLIDO("FALLIDO");

    private final String value;

    private EstadoEnvio(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
