package com.aug.ecommerce.infrastructure.messaging.publisher.eventpublisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.event.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.event.PagoConfirmadoEvent;
import com.aug.ecommerce.application.publisher.PagoEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class PagoEventPublisherImp implements PagoEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publicarPagoRealizado(IntegrationEvent evento) {
        if (evento instanceof PagoConfirmadoEvent clienteValido) {
            log.debug("---> Publica evento PagoConfirmadoEvent: {}", clienteValido);
            publisher.publishEvent(clienteValido);
        }
    }
}
