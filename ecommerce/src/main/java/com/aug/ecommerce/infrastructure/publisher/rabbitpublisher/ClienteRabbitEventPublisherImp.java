package com.aug.ecommerce.infrastructure.publisher.rabbitpublisher;

import com.aug.ecommerce.application.event.ClienteValidoEvent;
import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.ClienteEventPublisher;
import com.aug.ecommerce.infrastructure.queue.RabbitMQEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("rabbit")
public class ClienteRabbitEventPublisherImp implements ClienteEventPublisher {

    private final RabbitMQEventPublisher rabbitPublisher;

    @Override
    public void publishClienteValido(IntegrationEvent event) {
        if (event instanceof ClienteValidoEvent clienteValido) {
            if (clienteValido.ordenId() != 5L) {
                rabbitPublisher.publishEvent(event);
            }
        }
    }

    @Override
    public void publishClienteNoValido(IntegrationEvent event) {
        rabbitPublisher.publishEvent(event);
    }

}