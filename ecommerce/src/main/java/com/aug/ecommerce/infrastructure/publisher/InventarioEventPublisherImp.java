package com.aug.ecommerce.infrastructure.publisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.InventarioEventPublisher;
import com.aug.ecommerce.infrastructure.queue.RabbitMQIntegrationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventarioEventPublisherImp implements InventarioEventPublisher {

    private final ApplicationEventPublisher publisher;
    private final RabbitMQIntegrationEventPublisher rabbitPublisher;

    @Override
    public void publishStockDisponible(IntegrationEvent event) {
        rabbitPublisher.publishEvent(event);
    }

    @Override
    public void publishStockNoDisponible(IntegrationEvent event) {
        rabbitPublisher.publishEvent(event);
    }
}