package com.aug.ecommerce.infrastructure.publisher.eventpublisher;

import com.aug.ecommerce.application.event.*;
import com.aug.ecommerce.application.publisher.OrdenEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class OrdenEventPublisherImp implements OrdenEventPublisher {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publishOrdenCreated(IntegrationEvent event) {
        if (event instanceof OrdenCreadaEvent ordenCreadaEvent) {
            publisher.publishEvent(ordenCreadaEvent);
        }
    }

    @Override
    public void publishOrderPaymentRequested(IntegrationEvent event) {
        if (event instanceof OrdenAPagarEvent ordenAPagarEvent) {
            publisher.publishEvent(ordenAPagarEvent);
        }
    }

    @Override
    public void publishOrdenEnvioRequested(IntegrationEvent event) {
        if (event instanceof EnvioRequestedEvent envioRequestedEvent) {
            publisher.publishEvent(envioRequestedEvent);
        }
    }
}
