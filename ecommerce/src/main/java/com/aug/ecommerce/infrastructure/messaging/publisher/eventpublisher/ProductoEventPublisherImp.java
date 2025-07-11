package com.aug.ecommerce.infrastructure.messaging.publisher.eventpublisher;

import com.aug.ecommerce.application.events.*;
import com.aug.ecommerce.application.publishers.ProductoEventPublisher;
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
        if (event instanceof ProductoValidadoEvent productoValidadoEvent) {
            publisher.publishEvent(productoValidadoEvent);
        }
    }

    @Override
    public void publishProductoNoValido(IntegrationEvent event) {
        if (event instanceof ProductoNoValidadoEvent productoNoValidadoEvent) {
            publisher.publishEvent(productoNoValidadoEvent);
        }
    }
}
