package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.ProductoCreadoEvent;

public interface ProductoEventPublisher {
    void publicarProductoCreado(ProductoCreadoEvent productoCreadoEvent);
}
