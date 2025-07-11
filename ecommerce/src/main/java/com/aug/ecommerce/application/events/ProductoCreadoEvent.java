package com.aug.ecommerce.application.events;

import java.io.Serializable;

public record ProductoCreadoEvent(
        Long productoId,
        Long cantidad
)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "producto.inventario.crear";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
