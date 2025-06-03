package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.event.ProductoCreadoEvent;
import com.aug.ecommerce.application.event.ProductoNoValidoEvent;
import com.aug.ecommerce.application.event.ProductoValidoEvent;

public interface ProductoEventPublisher {
    void publicarProductoCreado(IntegrationEvent productoCreadoEvent);
    void publishProductoValido(IntegrationEvent event);
    void publishProductoNoValido(IntegrationEvent event);
}
