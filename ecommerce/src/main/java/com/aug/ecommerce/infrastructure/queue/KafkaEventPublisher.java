
package com.aug.ecommerce.infrastructure.queue;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("kafka")
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final EventTypeResolver eventTypeResolver;
    private final ObjectMapper objectMapper;

    public void publicar(String topic, IntegrationEvent event) {
        try {
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
            String mensaje = objectMapper.writeValueAsString(wrapper);
            kafkaTemplate.send(topic, event.getTraceId(), mensaje);
            log.info("Kafka - Evento publicado en [{}]: {}", topic, mensaje);
        } catch (Exception e) {
            log.error("Error al publicar evento en Kafka", e);
        }
    }
}
