package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.StockDisponibleEvent;
import com.aug.ecommerce.application.event.StockNoDisponibleEvent;

public interface InventarioEventPublisher {
    void publishStockDisponible(StockDisponibleEvent event);
    void publishStockNoDisponible(StockNoDisponibleEvent event);
}
