package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.IntegrationEvent;

public interface InventarioEventPublisher {
    void publishStockDisponible(IntegrationEvent event);
    void publishStockNoDisponible(IntegrationEvent event);
}
