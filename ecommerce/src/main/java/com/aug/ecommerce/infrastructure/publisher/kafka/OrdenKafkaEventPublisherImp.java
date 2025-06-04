
package com.aug.ecommerce.infrastructure.publisher.kafka;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.OrdenEventPublisher;
import com.aug.ecommerce.infrastructure.queue.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
@RequiredArgsConstructor
public class OrdenKafkaEventPublisherImp implements OrdenEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;

    @Override
    public void publishOrdenCreated(IntegrationEvent event) {
        kafkaEventPublisher.publicar("ordenes", event);
    }

    @Override
    public void publishOrderPaymentRequested(IntegrationEvent event) {
        kafkaEventPublisher.publicar("ordenes", event);
    }

    @Override
    public void publishOrdenEnvioRequested(IntegrationEvent event) {
        kafkaEventPublisher.publicar("ordenes", event);
    }
}
