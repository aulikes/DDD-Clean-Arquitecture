package com.aug.ecommerce.infrastructure.listener.rabbitlistener;

import com.aug.ecommerce.application.event.OrderPaymentRequestedEvent;
import com.aug.ecommerce.application.service.PagoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PagoRabbitListener {

    private final PagoService pagoService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "orden-events-queue")
    public void realizarPagoDeOrden(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.a-pagar".equals(wrapper.getEventType())) {
                OrderPaymentRequestedEvent event = objectMapper.readValue(payload, OrderPaymentRequestedEvent.class);
                log.debug("---> Entrando al RabbitListener PagoRabbitListener - realizarPagoDeOrden {}", event.ordenId());
                pagoService.realizarPago(event);
            } else
                log.warn("### onOrdenListaParaPago -> Evento de orden no reconocido: {}", wrapper.getEventType());
        } catch (Exception e) {
            log.error("Error en PagoRabbitListener", e);
        }
    }
}
