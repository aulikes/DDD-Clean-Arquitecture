package com.aug.ecommerce.application.publishers;

import com.aug.ecommerce.application.events.IntegrationEvent;

public interface InventarioEventPublisher {
    void publishStockDisponible(IntegrationEvent event);
    void publishStockNoDisponible(IntegrationEvent event);
}
