
package com.aug.ecommerce.infrastructure.messaging;

import com.aug.ecommerce.application.events.IntegrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("kafka")
@RequiredArgsConstructor
public class KafkaEventPublisher<T extends IntegrationEvent> {

    private final KafkaTemplate<String, IntegrationEventWrapper<T>> kafkaTemplate;
    private final EventTypeResolver eventTypeResolver;

    public void publicar(String topic, T event) {
        try {
            String eventType = eventTypeResolver.resolveEventType(event);
            String routingKey = eventType + "." + event.getVersion();

            // Envolver el evento con metadatos
            IntegrationEventWrapper<T> wrapper = new IntegrationEventWrapper<>(
                    eventType,
                    event.getVersion(),
                    event.getTraceId(),
                    event.getTimestamp(),
                    event
            );
            kafkaTemplate.send(topic, event.getTraceId(), wrapper);
            log.info("Kafka - Evento publicado en [{}]: {}", topic, wrapper);
        } catch (Exception e) {
            log.error("Error al publicar evento en Kafka", e);
        }
    }
}
