
package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.OrdenPagadaEvent;
import com.aug.ecommerce.application.services.EnvioService;
import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("kafka")
public class EnvioKafkaListener {

    private final EnvioService service;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "#{@producerKafka.ordenPagadaTopic}",
            groupId = "#{@consumerKafka.ordenEnvioPrepararGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void onMessage(ConsumerRecord<String, IntegrationEventWrapper<OrdenPagadaEvent>> payload) {
        try {
            IntegrationEventWrapper<OrdenPagadaEvent> wrapper = payload.value();
            OrdenPagadaEvent event = objectMapper.convertValue(wrapper.data(), OrdenPagadaEvent.class);
            service.crearEnvio(event.ordenId(), event.direccionEnvio());
        } catch (Exception e) {
            log.error("Error en EnvioKafkaListener", e);
        }
    }
}
