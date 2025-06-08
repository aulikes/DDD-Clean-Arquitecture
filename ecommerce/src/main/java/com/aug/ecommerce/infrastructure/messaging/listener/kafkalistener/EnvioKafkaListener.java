
package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.event.OrdenPagadaEvent;
import com.aug.ecommerce.application.service.EnvioService;
import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("kafka")
public class EnvioKafkaListener {

    private final EnvioService service;
    private final ObjectMapper objectMapper;

    public EnvioKafkaListener(EnvioService service,
                                ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "#{@producerKafka.ordenPagadaTopic}",
            groupId = "#{@consumerKafka.ordenEnvioPrepararGroupId}")
    public void prepararEnvio(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.envio.preparar".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), OrdenPagadaEvent.class);
                log.debug("---> Entrando a EnvioKafkaListener - onOrdenPagada {}", event.ordenId());
                service.crearEnvio(event.ordenId(), event.direccionEnvio());
            } else
                log.warn("### prepararEnvio -> Evento de envio no reconocido: {}", wrapper.eventType());
        } catch (Exception e) {
            log.error("Error en EnvioKafkaListener", e);
        }
    }
}
