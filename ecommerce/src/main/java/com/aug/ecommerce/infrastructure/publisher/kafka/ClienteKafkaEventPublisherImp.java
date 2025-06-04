
package com.aug.ecommerce.infrastructure.publisher.kafka;

import com.aug.ecommerce.application.event.ClienteValidoEvent;
import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.ClienteEventPublisher;
import com.aug.ecommerce.infrastructure.queue.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
@RequiredArgsConstructor
public class ClienteKafkaEventPublisherImp implements ClienteEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;

    @Override
    public void publishClienteValido(IntegrationEvent event) {
        if (event instanceof ClienteValidoEvent clienteValido) {
            if (clienteValido.ordenId() != 5L) {
                kafkaEventPublisher.publicar("clientes", event);
            }
        }
    }

    @Override
    public void publishClienteNoValido(IntegrationEvent event) {
        kafkaEventPublisher.publicar("clientes", event);
    }
}
