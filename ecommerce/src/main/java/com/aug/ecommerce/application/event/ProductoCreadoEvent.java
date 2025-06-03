package com.aug.ecommerce.application.event;

import java.io.Serializable;

public record ProductoCreadoEvent(
        Long productoId,
        Long cantidad
)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "producto.creado";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
