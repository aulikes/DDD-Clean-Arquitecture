package com.aug.ecommerce.application.events;

import java.io.Serializable;

public record ProductoValidadoEvent(Long ordenId)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "producto.orden.valido";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
