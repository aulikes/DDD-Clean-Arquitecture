package com.aug.ecommerce.infrastructure.messaging.publisher.eventpublisher;

import com.aug.ecommerce.application.event.EnvioPreparadoEvent;
import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.EnvioEventPublisher;
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
