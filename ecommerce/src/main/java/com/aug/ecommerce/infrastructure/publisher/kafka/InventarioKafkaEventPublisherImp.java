
package com.aug.ecommerce.infrastructure.publisher.kafka;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.InventarioEventPublisher;
import com.aug.ecommerce.infrastructure.queue.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
@RequiredArgsConstructor
public class InventarioKafkaEventPublisherImp implements InventarioEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;

    @Override
    public void publishStockDisponible(IntegrationEvent event) {
        kafkaEventPublisher.publicar("inventarios", event);
    }

    @Override
    public void publishStockNoDisponible(IntegrationEvent event) {
        kafkaEventPublisher.publicar("inventarios", event);
    }
}
