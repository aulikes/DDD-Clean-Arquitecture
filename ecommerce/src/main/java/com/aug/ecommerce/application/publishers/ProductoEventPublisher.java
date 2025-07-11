package com.aug.ecommerce.application.publishers;

import com.aug.ecommerce.application.events.IntegrationEvent;

public interface ProductoEventPublisher {
    void publicarProductoCreado(IntegrationEvent event);
    void publishProductoValido(IntegrationEvent event);
    void publishProductoNoValido(IntegrationEvent event);
}
