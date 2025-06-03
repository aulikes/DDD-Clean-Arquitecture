package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.event.StockDisponibleEvent;
import com.aug.ecommerce.application.event.StockNoDisponibleEvent;

public interface InventarioEventPublisher {
    void publishStockDisponible(IntegrationEvent event);
    void publishStockNoDisponible(IntegrationEvent event);
}
