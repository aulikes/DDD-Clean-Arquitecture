package com.aug.ecommerce.infrastructure.publisher.eventpublisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.event.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.publisher.PagoEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class PagoEventPublisherImp implements PagoEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publicarPagoRealizado(IntegrationEvent evento) {
        if (evento instanceof OrdenPreparadaParaPagoEvent clienteValido) {
            publisher.publishEvent(clienteValido);
        }
    }
}
