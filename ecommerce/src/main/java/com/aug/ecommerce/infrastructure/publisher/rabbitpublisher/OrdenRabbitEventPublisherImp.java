package com.aug.ecommerce.infrastructure.publisher.rabbitpublisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.OrdenEventPublisher;
import com.aug.ecommerce.infrastructure.queue.RabbitMQEventPublisher;
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
    public void publishOrderPaymentRequested(IntegrationEvent event) {
        rabbitPublisher.publishEvent(event);
    }

    @Override
    public void publishOrdenEnvioRequested(IntegrationEvent event) {
        rabbitPublisher.publishEvent(event);
    }
}
