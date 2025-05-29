package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.ProductoCreadoEvent;
import com.aug.ecommerce.application.event.ProductoNoValidoEvent;
import com.aug.ecommerce.application.event.ProductoValidoEvent;

public interface ProductoEventPublisher {
    void publicarProductoCreado(ProductoCreadoEvent productoCreadoEvent);
    void publishProductoValido(ProductoValidoEvent event);
    void publishProductoNoValido(ProductoNoValidoEvent event);
}
