package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.IntegrationEvent;

public interface ProductoEventPublisher {
    void publicarProductoCreado(IntegrationEvent event);
    void publishProductoValido(IntegrationEvent event);
    void publishProductoNoValido(IntegrationEvent event);
}
