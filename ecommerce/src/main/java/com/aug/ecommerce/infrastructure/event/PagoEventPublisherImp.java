package com.aug.ecommerce.infrastructure.event;

import com.aug.ecommerce.application.event.PagoConfirmadoEvent;
import com.aug.ecommerce.application.publisher.PagoEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagoEventPublisherImp implements PagoEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publicarPagoRealizado(PagoConfirmadoEvent evento) {
        publisher.publishEvent(evento);
    }
}
