package com.aug.ecommerce.infrastructure.event;

import com.aug.ecommerce.application.event.StockDisponibleEvent;
import com.aug.ecommerce.application.event.StockNoDisponibleEvent;
import com.aug.ecommerce.application.publisher.InventarioEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventarioEventPublisherImp implements InventarioEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publishStockDisponible(StockDisponibleEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishStockNoDisponible(StockNoDisponibleEvent event) {
        publisher.publishEvent(event);
    }
}