package com.aug.ecommerce.application.event;

import java.io.Serializable;

public record ProductoValidoEvent(Long ordenId)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "producto.orden.valido";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
