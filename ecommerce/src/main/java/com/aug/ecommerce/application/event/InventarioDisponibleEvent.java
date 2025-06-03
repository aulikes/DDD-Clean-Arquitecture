package com.aug.ecommerce.application.event;

import java.io.Serializable;

public record InventarioDisponibleEvent(Long ordenId)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "inventario.orden.disponible";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
