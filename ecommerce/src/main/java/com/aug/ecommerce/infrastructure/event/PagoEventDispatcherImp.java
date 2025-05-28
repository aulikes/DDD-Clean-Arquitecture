package com.aug.ecommerce.infrastructure.event;

import com.aug.ecommerce.application.gateway.PagoEventDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagoEventDispatcherImp implements PagoEventDispatcher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publicarPagoRealizado(Object evento) {
        publisher.publishEvent(evento);
    }
}
