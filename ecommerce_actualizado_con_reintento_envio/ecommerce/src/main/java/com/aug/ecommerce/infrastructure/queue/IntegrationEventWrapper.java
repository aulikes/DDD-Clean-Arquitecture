package com.aug.ecommerce.infrastructure.queue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Wrapper genérico para eventos publicados en RabbitMQ.
 * Incluye metadata común y el payload del evento.
 */
public class IntegrationEventWrapper<T> {

    private final String eventType;
    private final String version;
    private final String traceId;
    private final String timestamp;
    private final T data;

    @JsonCreator
    public IntegrationEventWrapper(
            @JsonProperty("eventType") String eventType,
            @JsonProperty("version") String version,
            @JsonProperty("traceId") String traceId,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("data") T data) {
        this.eventType = eventType;
        this.version = version;
        this.traceId = traceId;
        this.timestamp = timestamp;
        this.data = data;
    }

    public static <T> IntegrationEventWrapper<T> wrap(
            T data, String eventType, String version, String traceId, String timestamp) {
        return new IntegrationEventWrapper<>(eventType, version, traceId, timestamp, data);
    }

    public String getEventType() {
        return eventType;
    }

    public String getVersion() {
        return version;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public T getData() {
        return data;
    }
}
