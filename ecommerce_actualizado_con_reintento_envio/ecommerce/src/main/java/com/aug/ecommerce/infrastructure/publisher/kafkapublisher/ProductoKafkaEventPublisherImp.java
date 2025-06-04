
package com.aug.ecommerce.infrastructure.publisher.kafkapublisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.ProductoEventPublisher;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.infrastructure.queue.KafkaEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class ProductoKafkaEventPublisherImp implements ProductoEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;
    private final AppProperties.Kafka.Producer producer;

    public ProductoKafkaEventPublisherImp(KafkaEventPublisher kafkaEventPublisher, AppProperties appProperties) {
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
