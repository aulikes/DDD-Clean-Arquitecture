package com.aug.ecommerce.infrastructure.messaging.publisher.kafkapublisher;

import com.aug.ecommerce.application.events.ClienteValidadoEvent;
import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.publishers.ClienteEventPublisher;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.infrastructure.messaging.KafkaEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class ClienteKafkaEventPublisherImp implements ClienteEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;
    private final AppProperties.Kafka.Producer producer;

    public ClienteKafkaEventPublisherImp(KafkaEventPublisher kafkaEventPublisher, AppProperties appProperties) {
        this.kafkaEventPublisher = kafkaEventPublisher;
        this.producer = appProperties.getKafka().getProducer();
    }

    @Override
    public void publishClienteValido(IntegrationEvent event) {
        if (event instanceof ClienteValidadoEvent(Long ordenId)) {
            if (ordenId != 5L) {
                kafkaEventPublisher.publicar(producer.getClienteValidadoTopic(), event);
            }
        }
    }

    @Override
    public void publishClienteNoValido(IntegrationEvent event) {
        kafkaEventPublisher.publicar(producer.getClienteNoValidadoTopic(), event);
    }
}
