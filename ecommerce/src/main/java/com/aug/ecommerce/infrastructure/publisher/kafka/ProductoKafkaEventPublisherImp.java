
package com.aug.ecommerce.infrastructure.publisher.kafka;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.ProductoEventPublisher;
import com.aug.ecommerce.infrastructure.queue.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
@RequiredArgsConstructor
public class ProductoKafkaEventPublisherImp implements ProductoEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;

    @Override
    public void publicarProductoCreado(IntegrationEvent event) {
        kafkaEventPublisher.publicar("productos", event);
    }

    @Override
    public void publishProductoValido(IntegrationEvent event) {
        kafkaEventPublisher.publicar("productos", event);
    }

    @Override
    public void publishProductoNoValido(IntegrationEvent event) {
        kafkaEventPublisher.publicar("productos", event);
    }
}
