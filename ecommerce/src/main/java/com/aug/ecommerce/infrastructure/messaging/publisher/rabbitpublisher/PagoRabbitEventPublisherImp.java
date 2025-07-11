package com.aug.ecommerce.infrastructure.messaging.publisher.rabbitpublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.publishers.PagoEventPublisher;
import com.aug.ecommerce.infrastructure.messaging.RabbitMQEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("rabbit")
public class PagoRabbitEventPublisherImp implements PagoEventPublisher {

    private final ApplicationEventPublisher publisher;
    private final RabbitMQEventPublisher rabbitPublisher;

    @Override
    public void publicarPagoRealizado(IntegrationEvent evento) {
        rabbitPublisher.publishEvent(evento);
    }
}
