
package com.aug.ecommerce.infrastructure.messaging.publisher.kafkapublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.publishers.EnvioEventPublisher;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.infrastructure.messaging.KafkaEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class EnvioKafkaEventPublisherImp implements EnvioEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;
    private final AppProperties.Kafka.Producer producer;

    public EnvioKafkaEventPublisherImp(KafkaEventPublisher kafkaEventPublisher, AppProperties appProperties) {
        this.kafkaEventPublisher = kafkaEventPublisher;
        this.producer = appProperties.getKafka().getProducer();
    }

    @Override
    public void publicarEnvioPreparado(IntegrationEvent evento) {
        kafkaEventPublisher.publicar(producer.getEnvioPreparadoTopic(), evento);
    }

}
