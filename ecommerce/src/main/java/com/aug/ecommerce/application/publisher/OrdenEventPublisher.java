package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.IntegrationEvent;

/**
 * Publicador de eventos relacionados con la orden.
 * Su implementación concreta vive en la capa de infraestructura.
 */
public interface OrdenEventPublisher {

    void publishOrdenCreated(IntegrationEvent event);

    void publishOrderPaymentRequested(IntegrationEvent event);

    void publishOrdenEnvioRequested(IntegrationEvent event);
}
