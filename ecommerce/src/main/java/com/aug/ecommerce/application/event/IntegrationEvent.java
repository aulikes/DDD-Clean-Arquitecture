package com.aug.ecommerce.application.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Contrato base para todos los eventos que se publican en RabbitMQ.
 * Cada evento debe extender esta clase.
 */
public interface IntegrationEvent {

    /**
     * ID único del evento, útil para trazabilidad.
     */
    default String getEventId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Marca de tiempo del evento.
     */
    default Instant getTimestamp() {
        return Instant.now();
    }

    /**
     * Nombre lógico del evento, usado como header para identificación.
     * Ejemplo: "OrdenCreadaEvent"
     */
    String getEventType();

    /**
     * Versión del evento, útil para evolución sin romper consumidores.
     * Ejemplo: "v1"
     */
    String getVersion();

}
