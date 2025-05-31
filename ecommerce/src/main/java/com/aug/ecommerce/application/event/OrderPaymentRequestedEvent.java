package com.aug.ecommerce.application.event;

public record OrderPaymentRequestedEvent(
        Long ordenId,
        Double monto,
        String medioPago
) {}
