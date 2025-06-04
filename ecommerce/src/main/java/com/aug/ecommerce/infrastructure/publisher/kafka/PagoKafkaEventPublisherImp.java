
package com.aug.ecommerce.infrastructure.publisher.kafka;

import com.aug.ecommerce.application.event.IntegrationEvent;
import com.aug.ecommerce.application.publisher.PagoEventPublisher;
import com.aug.ecommerce.infrastructure.queue.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
@RequiredArgsConstructor
public class PagoKafkaEventPublisherImp implements PagoEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;

    @Override
    public void publicarPagoRealizado(IntegrationEvent evento) {
        kafkaEventPublisher.publicar("pagos", evento);
    }

}
