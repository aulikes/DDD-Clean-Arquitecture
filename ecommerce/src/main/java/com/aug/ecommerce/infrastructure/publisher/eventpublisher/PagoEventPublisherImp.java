package com.aug.ecommerce.infrastructure.publisher.eventpublisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.event.OrdenAPagarEvent;
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
        if (evento instanceof OrdenAPagarEvent clienteValido) {
            publisher.publishEvent(clienteValido);
        }
    }
}
