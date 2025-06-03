package com.aug.ecommerce.infrastructure.publisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.OrdenEventPublisher;
import com.aug.ecommerce.infrastructure.queue.RabbitMQIntegrationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdenEventPublisherImp implements OrdenEventPublisher {
    private final ApplicationEventPublisher publisher;
    private final RabbitMQIntegrationEventPublisher rabbitPublisher;

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
