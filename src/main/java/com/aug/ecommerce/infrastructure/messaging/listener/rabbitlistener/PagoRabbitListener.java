package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.events.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.services.PagoService;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("rabbit")
public class PagoRabbitListener {

    private final PagoService pagoService;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    public PagoRabbitListener(PagoService pagoService, ObjectMapper objectMapper, AppProperties appProperties) {
        this.pagoService = pagoService;
        this.objectMapper = objectMapper;
        this.appProperties = appProperties;
    }

    @RabbitListener(queues = "orden.pago.solicitar.v1.queue")
    public void realizarPagoDeOrden(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.pago.solicitar".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), OrdenPreparadaParaPagoEvent.class);
                log.debug("---> Entrando a PagoRabbitListener - realizarPagoDeOrden {}", event.ordenId());
                pagoService.realizarPago(event);
            } else
                log.warn("### realizarPagoDeOrden -> Evento de orden no reconocido: {}", wrapper.eventType());
        } catch (Exception e) {
            log.error("Error en PagoRabbitListener", e);
        }
    }
}
