
package com.aug.ecommerce.infrastructure.listener.kafkalistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("kafka")
public class OrdenKafkaListener {

    @KafkaListener(topics = "ordens", groupId = "orden-consumer-group")
    public void recibirMensaje(String mensaje) {
        log.info("📩 Kafka - Orden recibido: {}", mensaje);
    }
}
