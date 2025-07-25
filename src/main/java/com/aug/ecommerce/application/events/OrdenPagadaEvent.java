package com.aug.ecommerce.application.events;

import java.io.Serializable;

public record OrdenPagadaEvent(Long ordenId, String direccionEnvio)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "orden.envio.preparar";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
