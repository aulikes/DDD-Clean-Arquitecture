package com.aug.ecommerce.infrastructure.listener.rabbitlistener;

import com.aug.ecommerce.application.event.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.service.PagoService;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.infrastructure.queue.IntegrationEventWrapper;
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
            if ("orden.pago.solicitar".equals(wrapper.getEventType())) {
                var event = objectMapper.convertValue(wrapper.getData(), OrdenPreparadaParaPagoEvent.class);
                log.debug("---> Entrando a PagoRabbitListener - realizarPagoDeOrden {}", event.ordenId());
                pagoService.realizarPago(event);
            } else
                log.warn("### realizarPagoDeOrden -> Evento de orden no reconocido: {}", wrapper.getEventType());
        } catch (Exception e) {
            log.error("Error en PagoRabbitListener", e);
        }
    }
}
