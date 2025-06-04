package com.aug.ecommerce.application.event;

import java.io.Serializable;

public record InventarioNoValidadoEvent(Long ordenId)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "inventario.orden.no-disponible";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
