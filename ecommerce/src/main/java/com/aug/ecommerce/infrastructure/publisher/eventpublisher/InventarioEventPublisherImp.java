package com.aug.ecommerce.infrastructure.publisher.eventpublisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.event.InventarioDisponibleEvent;
import com.aug.ecommerce.application.event.InventarioNoDisponibleEvent;
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
        if (event instanceof InventarioDisponibleEvent inventarioDisponibleEvent) {
            publisher.publishEvent(inventarioDisponibleEvent);
        }
    }

    @Override
    public void publishStockNoDisponible(IntegrationEvent event) {
        if (event instanceof InventarioNoDisponibleEvent inventarioNoDisponibleEvent) {
            publisher.publishEvent(inventarioNoDisponibleEvent);
        }
    }
}