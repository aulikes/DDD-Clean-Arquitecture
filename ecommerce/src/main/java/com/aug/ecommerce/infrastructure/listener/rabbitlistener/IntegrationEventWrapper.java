package com.aug.ecommerce.infrastructure.listener.rabbitlistener;

/**
 * Clase para leer metadatos básicos antes de deserializar el evento completo.
 */
public class IntegrationEventWrapper {
    private String eventType;
    private String version;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
