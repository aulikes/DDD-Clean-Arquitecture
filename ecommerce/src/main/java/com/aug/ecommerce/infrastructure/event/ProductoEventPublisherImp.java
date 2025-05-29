package com.aug.ecommerce.infrastructure.event;

import com.aug.ecommerce.application.event.ProductoCreadoEvent;
import com.aug.ecommerce.application.event.ProductoNoValidoEvent;
import com.aug.ecommerce.application.event.ProductoValidoEvent;
import com.aug.ecommerce.application.publisher.ProductoEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductoEventPublisherImp implements ProductoEventPublisher {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publicarProductoCreado(ProductoCreadoEvent productoCreadoEvent) {
        publisher.publishEvent(productoCreadoEvent);
    }

    @Override
    public void publishProductoValido(ProductoValidoEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishProductoNoValido(ProductoNoValidoEvent event) {
        publisher.publishEvent(event);
    }
}
