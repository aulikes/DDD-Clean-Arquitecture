package com.aug.ecommerce.infrastructure.publisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.EnvioEventPublisher;
import com.aug.ecommerce.infrastructure.queue.RabbitMQEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnvioEventPublisherImp implements EnvioEventPublisher {

    private final ApplicationEventPublisher publisher;
    private final RabbitMQEventPublisher rabbitPublisher;

    @Override
    public void publicarEnvioPreparado(IntegrationEvent evento) {
        rabbitPublisher.publishEvent(evento);
    }
}
