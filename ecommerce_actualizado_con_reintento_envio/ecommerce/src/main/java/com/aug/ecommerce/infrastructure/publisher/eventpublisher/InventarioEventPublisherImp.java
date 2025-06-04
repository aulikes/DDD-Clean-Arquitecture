package com.aug.ecommerce.infrastructure.publisher.eventpublisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.event.InventarioValidadoEvent;
import com.aug.ecommerce.application.event.InventarioNoValidadoEvent;
import com.aug.ecommerce.application.publisher.InventarioEventPublisher;
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