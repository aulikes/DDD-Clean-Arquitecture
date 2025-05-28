package com.aug.ecommerce.application.event;

public record OrderPaymentRequestedEvent(
        Long ordenId,
        String direccionEntrega,
        Double monto,
        String medioPago
) {}
