package com.aug.ecommerce.application.event;

import java.io.Serializable;

public record StockNoDisponibleEvent(Long ordenId)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "inventario.no-disponible";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
