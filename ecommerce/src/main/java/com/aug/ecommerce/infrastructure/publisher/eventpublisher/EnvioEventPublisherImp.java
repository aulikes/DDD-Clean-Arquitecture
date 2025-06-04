package com.aug.ecommerce.infrastructure.publisher.eventpublisher;

import com.aug.ecommerce.application.event.ClienteValidoEvent;
import com.aug.ecommerce.application.event.EnvioPreparadoEvent;
import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.EnvioEventPublisher;
import com.aug.ecommerce.infrastructure.queue.RabbitMQEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class EnvioEventPublisherImp implements EnvioEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publicarEnvioPreparado(IntegrationEvent evento) {
        if (evento instanceof EnvioPreparadoEvent envioPreparadoEvent) {
            publisher.publishEvent(envioPreparadoEvent);
        }
    }
}
