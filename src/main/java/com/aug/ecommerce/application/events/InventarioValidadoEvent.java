package com.aug.ecommerce.application.events;

import java.io.Serializable;

public record InventarioValidadoEvent(Long ordenId)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "inventario.orden.disponible";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
