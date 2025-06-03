package com.aug.ecommerce.application.event;

import java.io.Serializable;

public record ClienteNoValidoEvent(Long ordenId) implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "cliente.no-valido";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
