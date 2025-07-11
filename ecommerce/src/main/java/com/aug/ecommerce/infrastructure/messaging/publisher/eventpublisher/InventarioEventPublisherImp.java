package com.aug.ecommerce.infrastructure.messaging.publisher.eventpublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.events.InventarioValidadoEvent;
import com.aug.ecommerce.application.events.InventarioNoValidadoEvent;
import com.aug.ecommerce.application.publishers.InventarioEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class InventarioEventPublisherImp implements InventarioEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publishStockDisponible(IntegrationEvent event) {
        if (event instanceof InventarioValidadoEvent inventarioValidadoEvent) {
            publisher.publishEvent(inventarioValidadoEvent);
        }
    }

    @Override
    public void publishStockNoDisponible(IntegrationEvent event) {
        if (event instanceof InventarioNoValidadoEvent inventarioNoValidadoEvent) {
            publisher.publishEvent(inventarioNoValidadoEvent);
        }
    }
}