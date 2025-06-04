package com.aug.ecommerce.application.event;

import java.io.Serializable;

public record OrdenPreparadaParaPagoEvent(
        Long ordenId,
        Double monto,
        String medioPago
)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "orden.pago.solicitar";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
