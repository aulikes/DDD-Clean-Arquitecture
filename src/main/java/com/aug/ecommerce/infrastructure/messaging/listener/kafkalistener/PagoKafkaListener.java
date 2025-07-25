
package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.services.PagoService;
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
public class PagoKafkaListener {

    private final PagoService pagoService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "#{@producerKafka.ordenPreparadaPagoTopic}",
            groupId = "#{@consumerKafka.ordenPagoSolicitarGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void onMessage(ConsumerRecord<String, IntegrationEventWrapper<OrdenPreparadaParaPagoEvent>> payload) {
        try {
            IntegrationEventWrapper<OrdenPreparadaParaPagoEvent> wrapper = payload.value();
            OrdenPreparadaParaPagoEvent event = objectMapper.convertValue(wrapper.data(), OrdenPreparadaParaPagoEvent.class);
            log.debug("---> Entrando a PagoKafkaListener - realizarPagoDeOrden {}", event.ordenId());
            pagoService.realizarPago(event);
        } catch (Exception e) {
            log.error("Error en PagoRabbitListener", e);
        }
    }
}
