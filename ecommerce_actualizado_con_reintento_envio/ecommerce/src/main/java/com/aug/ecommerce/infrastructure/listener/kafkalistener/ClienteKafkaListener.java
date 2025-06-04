
package com.aug.ecommerce.infrastructure.listener.kafkalistener;

import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.service.ClienteValidacionService;
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
public class ClienteKafkaListener {

    private final ClienteValidacionService service;
    private final ObjectMapper objectMapper;

    public ClienteKafkaListener(ClienteValidacionService service,
                                 ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "#{@producerKafka.ordenCreadaTopic}",
            groupId = "#{@consumerKafka.ordenClienteValidarGroupId}")
    public void validarCliente(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.multicast.creada".equals(wrapper.getEventType())) {
                var event = objectMapper.convertValue(wrapper.getData(), OrdenCreadaEvent.class);
                log.debug("---> Entrando a ClienteKafkaListener - OrdenCreadaEvent {}", event.ordenId());
                service.validarClienteCreacionOrden(event.ordenId(), event.clienteId());
            } else
                log.warn("### validarCliente -> Evento de cliente no reconocido: {}", wrapper.getEventType());

        } catch (Exception e) {
            log.error("Error procesando mensaje en ClienteKafkaListener", e);
        }
    }
}
