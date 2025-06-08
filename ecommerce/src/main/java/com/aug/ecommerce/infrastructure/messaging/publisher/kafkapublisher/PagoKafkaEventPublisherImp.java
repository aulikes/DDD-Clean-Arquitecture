
package com.aug.ecommerce.infrastructure.messaging.publisher.kafkapublisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.PagoEventPublisher;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.infrastructure.messaging.KafkaEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class PagoKafkaEventPublisherImp implements PagoEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;
    private final AppProperties.Kafka.Producer producer;

    public PagoKafkaEventPublisherImp(KafkaEventPublisher kafkaEventPublisher, AppProperties appProperties) {
        this.kafkaEventPublisher = kafkaEventPublisher;
        this.producer = appProperties.getKafka().getProducer();
    }

    @Override
    public void publicarPagoRealizado(IntegrationEvent evento) {
        kafkaEventPublisher.publicar(producer.getPagoRealizadoTopic(), evento);
    }

}
