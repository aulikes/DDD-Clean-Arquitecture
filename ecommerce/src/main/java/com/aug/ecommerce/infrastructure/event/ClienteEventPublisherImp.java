package com.aug.ecommerce.infrastructure.event;

import com.aug.ecommerce.application.event.ClienteNoValidoEvent;
import com.aug.ecommerce.application.event.ClienteValidoEvent;
import com.aug.ecommerce.application.publisher.ClienteEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteEventPublisherImp implements ClienteEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publishClienteValido(ClienteValidoEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishClienteNoValido(ClienteNoValidoEvent event) {
        publisher.publishEvent(event);
    }

}