package com.aug.ecommerce.infrastructure.queue;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Componente genérico interno que publica cualquier IntegrationEvent en RabbitMQ.
 */
@Component
@Slf4j
public class RabbitMQIntegrationEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final AppProperties.EventRabbitMQ eventRabbitMQ;
    private final ObjectMapper objectMapper;

    public RabbitMQIntegrationEventPublisher(
            RabbitTemplate rabbitTemplate, AppProperties appProperties, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventRabbitMQ = appProperties.getEventRabbitMQ();
        this.objectMapper = objectMapper;
    }

    public void publishEvent(IntegrationEvent event) {
        try {
            String routingKey = event.getEventType() + "." + event.getVersion();
            log.info("----->>>>> Publicando evento, con routingKey: {}", routingKey);
            byte[] body = objectMapper.writeValueAsBytes(event);

            Message message = getMessage(event, body);
            rabbitTemplate.setMandatory(true);
            rabbitTemplate.setReturnsCallback(returned -> {
                log.error("[RabbitMQ] Mensaje NO enrutado:");
                log.error("Routing Key: {}", returned.getRoutingKey());
                log.error("Reply Text : {}", returned.getReplyText());
            });
            rabbitTemplate.send(eventRabbitMQ.getExchange(), routingKey, message);
        } catch (Exception ex) {
            // Aquí se puede integrar un sistema de monitoreo o fallback
            log.error("Error publicando evento: " + ex.getMessage());
            throw new RuntimeException("No se pudo publicar evento", ex);
        }
    }

    private static Message getMessage(IntegrationEvent event, byte[] body) {
        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setHeader("x-event-id", event.getEventId());
        props.setHeader("x-event-type", event.getEventType());
        props.setHeader("x-event-timestamp", event.getTimestamp().toString());
        props.setHeader("x-event-version", event.getVersion());
        // Se pueden agregar x-request-id, x-user-id si se usa context-aware headers
        return new Message(body, props);
    }
}

