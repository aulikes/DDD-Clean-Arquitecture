package com.aug.ecommerce.application.publishers;

import com.aug.ecommerce.application.events.IntegrationEvent;

/**
 * Publicador de eventos relacionados con la orden.
 * Su implementación concreta vive en la capa de infraestructura.
 */
public interface OrdenEventPublisher {

    void publishOrdenCreated(IntegrationEvent event);

    void publishOrdenPagoRequerido(IntegrationEvent event);

    void publishOrdenEnvioRequerido(IntegrationEvent event);
}
