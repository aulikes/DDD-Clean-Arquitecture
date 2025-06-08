package com.aug.ecommerce.infrastructure.messaging.publisher.eventpublisher;

import com.aug.ecommerce.application.event.ClienteNoValidadoEvent;
import com.aug.ecommerce.application.event.ClienteValidadoEvent;
import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.ClienteEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class ClienteEventPublisherImp implements ClienteEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publishClienteValido(IntegrationEvent event) {
        if (event instanceof ClienteValidadoEvent clienteValidadoEvent) {
            if (clienteValidadoEvent.ordenId() != 5L) {
                publisher.publishEvent(clienteValidadoEvent);
            }
        }
    }

    @Override
    public void publishClienteNoValido(IntegrationEvent event) {
        if (event instanceof ClienteNoValidadoEvent clienteNoValidadoEvent) {
            publisher.publishEvent(clienteNoValidadoEvent);
        }
    }

}