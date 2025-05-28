package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.OrderPaymentRequestedEvent;

/**
 * Publicador de eventos relacionados con la orden.
 * Su implementación concreta vive en la capa de infraestructura.
 */
public interface OrderEventPublisher {
    void publishOrderPaymentRequested(OrderPaymentRequestedEvent event);
}
