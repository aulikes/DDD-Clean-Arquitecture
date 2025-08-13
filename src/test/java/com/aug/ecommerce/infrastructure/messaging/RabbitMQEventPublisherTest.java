package com.aug.ecommerce.infrastructure.messaging;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para RabbitMQEventPublisher.
 * - Verifica envoltura, headers AMQP, routingKey y manejo de errores.
 */
@ExtendWith(MockitoExtension.class)
class RabbitMQEventPublisherTest {

    @Mock RabbitTemplate rabbitTemplate;
    @Mock ObjectMapper objectMapper;
    @Mock EventTypeResolver eventTypeResolver;
    @Mock AppProperties appProperties;
    @Mock AppProperties.EventRabbitMQ eventRabbitMQ;

    /** Evento de prueba con timestamp/traceId determinísticos. */
    static class TestEvent implements IntegrationEvent {
        private final String version;
        private final String traceId;
        private final Instant ts;
        TestEvent(String version, String traceId, Instant ts) {
            this.version = version; this.traceId = traceId; this.ts = ts;
        }
        @Override public String getEventType() { return "TestEvent"; }
        @Override public String getVersion() { return version; }
        @Override public String getTraceId() { return traceId; }
        @Override public Instant getTimestamp() { return ts; }
    }

    private RabbitMQEventPublisher newPublisher() {
        when(appProperties.getEventRabbitMQ()).thenReturn(eventRabbitMQ);
        return new RabbitMQEventPublisher(rabbitTemplate, objectMapper, eventTypeResolver, appProperties);
    }

    @Test
    @DisplayName("publishEvent: construye wrapper, headers y publica en exchange/topic con routing esperado")
    void publishEvent_ok() throws Exception {
        var event = new TestEvent("v1", "trace-123", Instant.parse("2024-01-01T00:00:00Z"));
        when(eventTypeResolver.resolveEventType(event)).thenReturn("orden.creada");
        when(eventRabbitMQ.getExchangeTopic()).thenReturn("app.events");

        // Capturar el wrapper que se serializa y devolver un cuerpo JSON controlado
        ArgumentCaptor<Object> wrapperCaptor = ArgumentCaptor.forClass(Object.class);
        when(objectMapper.writeValueAsBytes(wrapperCaptor.capture()))
                .thenReturn("{\"ok\":true}".getBytes());

        var publisher = newPublisher();

        publisher.publishEvent(event);

        // Se publica al exchange y routing key esperados
        ArgumentCaptor<Message> msgCaptor = ArgumentCaptor.forClass(Message.class);
        verify(rabbitTemplate).convertAndSend(eq("app.events"), eq("orden.creada.v1"), msgCaptor.capture());

        // Verificar mensaje y headers
        Message msg = msgCaptor.getValue();
        assertArrayEquals("{\"ok\":true}".getBytes(), msg.getBody());
        MessageProperties props = msg.getMessageProperties();
        assertEquals(MessageProperties.CONTENT_TYPE_JSON, props.getContentType());
        assertEquals(event.getTimestamp(), props.getHeaders().get("x-event-timestamp"));
        assertEquals("orden.creada", props.getHeaders().get("x-event-type"));
        assertEquals("v1", props.getHeaders().get("x-event-version"));
        assertEquals("trace-123", props.getHeaders().get("x-trace-id"));

        // setMandatory(true) configurado
        verify(rabbitTemplate, atLeastOnce()).setMandatory(true);

        // Verificar IntegrationEventWrapper y sus metadatos
        Object wrapped = wrapperCaptor.getValue();
        assertTrue(wrapped instanceof IntegrationEventWrapper);
        @SuppressWarnings("unchecked")
        var iw = (IntegrationEventWrapper<IntegrationEvent>) wrapped;
        assertEquals("orden.creada", iw.eventType());
        assertEquals("v1", iw.version());
        assertEquals("trace-123", iw.traceId());
        assertEquals(event.getTimestamp(), iw.timestamp());
        assertSame(event, iw.data());
    }

    @Test
    @DisplayName("publishEvent: si la serialización falla, lanza RuntimeException con la causa")
    void publishEvent_fallaSerializacion() throws Exception {
        var event = new TestEvent("v2", "trace-x", Instant.now());
        when(eventTypeResolver.resolveEventType(event)).thenReturn("orden.creada");
        when(objectMapper.writeValueAsBytes(any())).thenThrow(new RuntimeException("boom"));

        var publisher = newPublisher();

        RuntimeException ex = assertThrows(RuntimeException.class, () -> publisher.publishEvent(event));
        assertTrue(ex.getMessage().contains("No se pudo publicar el evento"));
        assertNotNull(ex.getCause());
        verifyNoInteractions(rabbitTemplate); // no intenta publicar si no pudo serializar
    }

    @Test
    @DisplayName("publishEvent: si convertAndSend falla, relanza como RuntimeException")
    void publishEvent_fallaEnvio() throws Exception {
        var event = new TestEvent("v1", "trace-y", Instant.now());
        when(eventTypeResolver.resolveEventType(event)).thenReturn("orden.creada");
        when(eventRabbitMQ.getExchangeTopic()).thenReturn("app.events");
        when(objectMapper.writeValueAsBytes(any())).thenReturn("{}".getBytes());
        doThrow(new RuntimeException("broker down"))
                .when(rabbitTemplate).convertAndSend(eq("app.events"), eq("orden.creada.v1"), any(Message.class));

        var publisher = newPublisher();

        RuntimeException ex = assertThrows(RuntimeException.class, () -> publisher.publishEvent(event));
        assertTrue(ex.getMessage().contains("No se pudo publicar el evento"));
    }
}
