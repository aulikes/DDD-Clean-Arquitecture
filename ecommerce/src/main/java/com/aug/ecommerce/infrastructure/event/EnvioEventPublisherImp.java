package com.aug.ecommerce.infrastructure.event;

import com.aug.ecommerce.application.event.EnvioPreparadoEvent;
import com.aug.ecommerce.application.publisher.EnvioEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnvioEventPublisherImp implements EnvioEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publicarEnvioPreparado(EnvioPreparadoEvent evento) {
        publisher.publishEvent(evento);
    }
}
