package com.aug.ecommerce.application.event;

import java.io.Serializable;

public record ClienteNoValidoEvent(Long ordenId) implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "cliente.orden.no-valido";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
