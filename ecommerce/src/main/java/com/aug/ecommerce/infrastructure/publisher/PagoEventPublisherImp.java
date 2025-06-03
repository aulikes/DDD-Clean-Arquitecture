package com.aug.ecommerce.infrastructure.publisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.PagoEventPublisher;
import com.aug.ecommerce.infrastructure.queue.RabbitMQIntegrationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagoEventPublisherImp implements PagoEventPublisher {

    private final ApplicationEventPublisher publisher;
    private final RabbitMQIntegrationEventPublisher rabbitPublisher;

    @Override
    public void publicarPagoRealizado(IntegrationEvent evento) {
        rabbitPublisher.publishEvent(evento);
    }
}
