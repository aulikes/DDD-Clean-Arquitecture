package com.aug.ecommerce.application.events;

import java.io.Serializable;

public record ClienteNoValidadoEvent(Long ordenId) implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "cliente.orden.no-valido";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
