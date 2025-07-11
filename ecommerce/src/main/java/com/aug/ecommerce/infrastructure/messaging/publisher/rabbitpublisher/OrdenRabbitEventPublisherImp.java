package com.aug.ecommerce.infrastructure.messaging.publisher.rabbitpublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.publishers.OrdenEventPublisher;
import com.aug.ecommerce.infrastructure.messaging.RabbitMQEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("rabbit")
public class OrdenRabbitEventPublisherImp implements OrdenEventPublisher {
    private final RabbitMQEventPublisher rabbitPublisher;

    @Override
    public void publishOrdenCreated(IntegrationEvent event) {
        rabbitPublisher.publishEvent(event);
    }

    @Override
    public void publishOrdenPagoRequerido(IntegrationEvent event) {
        rabbitPublisher.publishEvent(event);
    }

    @Override
    public void publishOrdenEnvioRequerido(IntegrationEvent event) {
        rabbitPublisher.publishEvent(event);
    }
}
