package com.aug.ecommerce.application.event;

import java.io.Serializable;

public record OrderPaymentRequestedEvent(
        Long ordenId,
        Double monto,
        String medioPago
)  implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "orden.a-pagar";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
