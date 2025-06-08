package com.aug.ecommerce.infrastructure.messaging.publisher.rabbitpublisher;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.ProductoEventPublisher;
import com.aug.ecommerce.infrastructure.messaging.RabbitMQEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("rabbit")
public class ProductoRabbitEventPublisherImp implements ProductoEventPublisher {
    private final ApplicationEventPublisher publisher;
    private final RabbitMQEventPublisher rabbitPublisher;

    @Override
    public void publicarProductoCreado(IntegrationEvent productoCreadoEvent) {
        rabbitPublisher.publishEvent(productoCreadoEvent);
    }

    @Override
    public void publishProductoValido(IntegrationEvent event) {
        rabbitPublisher.publishEvent(event);
    }

    @Override
    public void publishProductoNoValido(IntegrationEvent event) {
        rabbitPublisher.publishEvent(event);
    }
}
