
package com.aug.ecommerce.infrastructure.messaging.publisher.kafkapublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.publishers.ProductoEventPublisher;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.infrastructure.messaging.KafkaEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class ProductoKafkaEventPublisherImp implements ProductoEventPublisher {

    private final KafkaEventPublisher<IntegrationEvent> kafkaEventPublisher;
    private final AppProperties.Kafka.Producer producer;

    public ProductoKafkaEventPublisherImp(
            KafkaEventPublisher<IntegrationEvent> kafkaEventPublisher, AppProperties appProperties) {
        this.kafkaEventPublisher = kafkaEventPublisher;
        this.producer = appProperties.getKafka().getProducer();
    }

    @Override
    public void publicarProductoCreado(IntegrationEvent event) {
        kafkaEventPublisher.publicar(producer.getProductoCreadoTopic(), event);
    }

    @Override
    public void publishProductoValido(IntegrationEvent event) {
        kafkaEventPublisher.publicar(producer.getProductoValidadoTopic(), event);
    }

    @Override
    public void publishProductoNoValido(IntegrationEvent event) {
        kafkaEventPublisher.publicar(producer.getProductoNoValidadoTopic(), event);
    }
}
