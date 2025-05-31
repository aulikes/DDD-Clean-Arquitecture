package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.EnvioRequestedEvent;
import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.event.OrderPaymentRequestedEvent;

/**
 * Publicador de eventos relacionados con la orden.
 * Su implementación concreta vive en la capa de infraestructura.
 */
public interface OrderEventPublisher {

    void publishOrdenCreated(OrdenCreadaEvent event);

    void publishOrderPaymentRequested(OrderPaymentRequestedEvent event);

    void publishOrdenEnvioRequested(EnvioRequestedEvent event);
}
