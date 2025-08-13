
package com.aug.ecommerce.infrastructure.messaging.publisher.kafkapublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.publishers.InventarioEventPublisher;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.infrastructure.messaging.KafkaEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class InventarioKafkaEventPublisherImp implements InventarioEventPublisher {

    private final KafkaEventPublisher<IntegrationEvent> kafkaEventPublisher;
    private final AppProperties.Kafka.Producer producer;

    public InventarioKafkaEventPublisherImp(
            KafkaEventPublisher<IntegrationEvent> kafkaEventPublisher, AppProperties appProperties) {
        this.kafkaEventPublisher = kafkaEventPublisher;
        this.producer = appProperties.getKafka().getProducer();
    }

    @Override
    public void publishStockDisponible(IntegrationEvent event) {
        kafkaEventPublisher.publicar(producer.getInventarioValidadoTopic(), event);
    }

    @Override
    public void publishStockNoDisponible(IntegrationEvent event) {
        kafkaEventPublisher.publicar(producer.getInventarioNoValidadoTopic(), event);
    }
}
