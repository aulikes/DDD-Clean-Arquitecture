
package com.aug.ecommerce.infrastructure.publisher.kafka;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.EnvioEventPublisher;
import com.aug.ecommerce.infrastructure.queue.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
@RequiredArgsConstructor
public class EnvioKafkaEventPublisherImp implements EnvioEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;

    @Override
    public void publicarEnvioPreparado(IntegrationEvent evento) {
        kafkaEventPublisher.publicar("envios", evento);
    }

}
