package com.aug.ecommerce.application.events;

import java.io.Serializable;

public record ClienteValidadoEvent(Long ordenId) implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "cliente.orden.valido";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}