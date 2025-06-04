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
    public void publishOrdenPagoRequerido(IntegrationEvent event) {
        if (event instanceof OrdenPreparadaParaPagoEvent ordenPreparadaParaPagoEvent) {
            publisher.publishEvent(ordenPreparadaParaPagoEvent);
        }
    }

    @Override
    public void publishOrdenEnvioRequerido(IntegrationEvent event) {
        if (event instanceof OrdenPagadaEvent ordenPagadaEvent) {
            publisher.publishEvent(ordenPagadaEvent);
        }
    }
}
