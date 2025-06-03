package com.aug.ecommerce.application.event;

import java.io.Serializable;

public record EnvioRequestedEvent(Long ordenId, String direccionEnvio)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "envio.pedido";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
