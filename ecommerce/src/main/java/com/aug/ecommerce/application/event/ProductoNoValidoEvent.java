package com.aug.ecommerce.application.event;

import java.io.Serializable;

public record ProductoNoValidoEvent(Long ordenId)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "producto.orden.no-valido";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
