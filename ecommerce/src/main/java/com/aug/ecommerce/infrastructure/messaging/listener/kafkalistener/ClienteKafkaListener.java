
package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.services.ClienteValidacionService;
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
public class ClienteKafkaListener {

    private final ClienteValidacionService service;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "#{@producerKafka.ordenCreadaTopic}",
            groupId = "#{@consumerKafka.ordenClienteValidarGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void onMessage(ConsumerRecord<String, IntegrationEventWrapper<OrdenCreadaEvent>> payload) {
        try {
            IntegrationEventWrapper<OrdenCreadaEvent> wrapper = payload.value();
            OrdenCreadaEvent event = objectMapper.convertValue(wrapper.data(), OrdenCreadaEvent.class);
            log.debug("---> Entrando a ClienteKafkaListener - OrdenCreadaEvent {}", event.ordenId());
            service.validarClienteCreacionOrden(event.ordenId(), event.clienteId());
        } catch (Exception e) {
            log.error("Error procesando mensaje en ClienteKafkaListener", e);
        }
    }
}
