
package com.aug.ecommerce.infrastructure.listener.kafkalistener;

import com.aug.ecommerce.application.event.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.service.PagoService;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.infrastructure.queue.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("kafka")
public class PagoKafkaListener {

    private final PagoService pagoService;
    private final ObjectMapper objectMapper;

    public PagoKafkaListener(PagoService pagoService, ObjectMapper objectMapper, AppProperties appProperties) {
        this.pagoService = pagoService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "#{@producerKafka.ordenPreparadaPagoTopic}",
            groupId = "#{@consumerKafka.ordenPagoSolicitarGroupId}")
    public void realizarPagoDeOrden(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.pago.solicitar".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), OrdenPreparadaParaPagoEvent.class);
                log.debug("---> Entrando a PagoKafkaListener - realizarPagoDeOrden {}", event.ordenId());
                pagoService.realizarPago(event);
            } else
                log.warn("### realizarPagoDeOrden -> Evento de orden no reconocido: {}", wrapper.eventType());
        } catch (Exception e) {
            log.error("Error en PagoRabbitListener", e);
        }
    }
}
