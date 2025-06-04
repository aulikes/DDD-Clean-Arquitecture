package com.aug.ecommerce.infrastructure.publisher.eventpublisher;

import com.aug.ecommerce.application.event.*;
import com.aug.ecommerce.application.publisher.ProductoEventPublisher;
import com.aug.ecommerce.infrastructure.queue.RabbitMQEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class ProductoEventPublisherImp implements ProductoEventPublisher {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publicarProductoCreado(IntegrationEvent event) {
        if (event instanceof ProductoCreadoEvent productoCreadoEvent) {
            publisher.publishEvent(productoCreadoEvent);
        }
    }

    @Override
    public void publishProductoValido(IntegrationEvent event) {
        if (event instanceof ProductoValidoEvent productoValidoEvent) {
            publisher.publishEvent(productoValidoEvent);
        }
    }

    @Override
    public void publishProductoNoValido(IntegrationEvent event) {
        if (event instanceof ProductoNoValidoEvent productoNoValidoEvent) {
            publisher.publishEvent(productoNoValidoEvent);
        }
    }
}
