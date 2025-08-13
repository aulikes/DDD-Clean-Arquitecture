
package com.aug.ecommerce.infrastructure.messaging.publisher.kafkapublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.publishers.OrdenEventPublisher;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.infrastructure.messaging.KafkaEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class OrdenKafkaEventPublisherImp implements OrdenEventPublisher {

    private final KafkaEventPublisher<IntegrationEvent> kafkaEventPublisher;
    private final AppProperties.Kafka.Producer producer;

    public OrdenKafkaEventPublisherImp(
            KafkaEventPublisher<IntegrationEvent> kafkaEventPublisher, AppProperties appProperties) {
        this.kafkaEventPublisher = kafkaEventPublisher;
        this.producer = appProperties.getKafka().getProducer();
    }

    @Override
    public void publishOrdenCreated(IntegrationEvent event) {
        kafkaEventPublisher.publicar(producer.getOrdenCreadaTopic(), event);
    }

    @Override
    public void publishOrdenPagoRequerido(IntegrationEvent event) {
        kafkaEventPublisher.publicar(producer.getOrdenPreparadaPagoTopic(), event);
    }

    @Override
    public void publishOrdenEnvioRequerido(IntegrationEvent event) {
        kafkaEventPublisher.publicar(producer.getOrdenPagadaTopic(), event);
    }
}
