package com.aug.ecommerce.application.event;

import java.io.Serializable;

public record StockDisponibleEvent(Long ordenId)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "inventario.disponible";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
