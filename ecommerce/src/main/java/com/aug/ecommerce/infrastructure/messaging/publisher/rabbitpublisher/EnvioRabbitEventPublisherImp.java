package com.aug.ecommerce.infrastructure.messaging.publisher.rabbitpublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.publishers.EnvioEventPublisher;
import com.aug.ecommerce.infrastructure.messaging.RabbitMQEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("rabbit")
public class EnvioRabbitEventPublisherImp implements EnvioEventPublisher {

    private final RabbitMQEventPublisher rabbitPublisher;

    @Override
    public void publicarEnvioPreparado(IntegrationEvent evento) {
        rabbitPublisher.publishEvent(evento);
    }
}
