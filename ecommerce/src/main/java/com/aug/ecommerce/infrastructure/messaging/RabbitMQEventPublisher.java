package com.aug.ecommerce.infrastructure.messaging;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final EventTypeResolver eventTypeResolver;
    private final AppProperties.EventRabbitMQ eventRabbitMQ;

    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper,
                                  EventTypeResolver eventTypeResolver, AppProperties appProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.eventTypeResolver = eventTypeResolver;
        this.eventRabbitMQ = appProperties.getEventRabbitMQ();
    }

    /**
     * Publica un evento de integración serializado en formato IntegrationEventWrapper.
     *
     * @param event El evento de integración a publicar
     */
    public void publishEvent(IntegrationEvent event) {
        try {
            // Resolver eventType (ej: "orden.creada")
            String eventType = eventTypeResolver.resolveEventType(event);
            String routingKey = eventType + "." + event.getVersion();

            // Envolver el evento con metadatos
            IntegrationEventWrapper<IntegrationEvent> wrapper = new IntegrationEventWrapper<>(
                    eventType,
                    event.getVersion(),
                    event.getTraceId(),
                    event.getTimestamp(),
                    event
            );

            // Serializar el wrapper a JSON
            byte[] body = objectMapper.writeValueAsBytes(wrapper);
            Message message = buildMessage(wrapper, body);

            // Log de auditoría
            log.info("[RabbitMQ] Publicando evento '{}', routingKey '{}'", eventType, routingKey);

            // necesario para activar returns
            rabbitTemplate.setMandatory(true);
            // Confirm callback (ACK/NACK)
            rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                if (ack) {
                    log.info("Message confirmed by broker");
                } else {
                    log.error("Message NOT confirmed: {}", cause);
                }
            });
            rabbitTemplate.setReturnsCallback(returned -> {
                log.error("[RabbitMQ] Mensaje NO enrutado:");
                log.error("  -> Routing Key: {}", returned.getRoutingKey());
                log.error("  -> Reply Text : {}", returned.getReplyText());
            });

            // Publicar al exchange tipo topic
            rabbitTemplate.convertAndSend(eventRabbitMQ.getExchangeTopic(), routingKey, message);

        } catch (Exception e) {
            log.error("Error al publicar evento en RabbitMQ", e);
            throw new RuntimeException("No se pudo publicar el evento", e);
        }
    }

    /**
     * Construye un mensaje AMQP con encabezados estándar para trazabilidad.
     *
     * @param wrapper Envoltorio del evento
     * @param body    Cuerpo serializado del mensaje
     * @return Mensaje con metadatos
     */
    private Message buildMessage(IntegrationEventWrapper<?> wrapper, byte[] body) {
        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setHeader("x-event-timestamp", wrapper.timestamp());
        props.setHeader("x-event-type", wrapper.eventType());
        props.setHeader("x-event-version", wrapper.version());
        props.setHeader("x-trace-id", wrapper.traceId());
        return new Message(body, props);
    }
}
