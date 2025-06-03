package com.aug.ecommerce.application.event;

import java.io.Serializable;

public record ClienteValidoEvent (Long ordenId) implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "cliente.valido";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}