package com.aug.ecommerce.infrastructure.publisher.eventpublisher;

import com.aug.ecommerce.application.event.ClienteNoValidoEvent;
import com.aug.ecommerce.application.event.ClienteValidoEvent;
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
        if (event instanceof ClienteValidoEvent clienteValidoEvent) {
            if (clienteValidoEvent.ordenId() != 5L) {
                publisher.publishEvent(clienteValidoEvent);
            }
        }
    }

    @Override
    public void publishClienteNoValido(IntegrationEvent event) {
        if (event instanceof ClienteNoValidoEvent clienteNoValidoEvent) {
            publisher.publishEvent(clienteNoValidoEvent);
        }
    }

}