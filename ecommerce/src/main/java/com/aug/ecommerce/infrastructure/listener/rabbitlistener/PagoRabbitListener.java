package com.aug.ecommerce.infrastructure.listener.rabbitlistener;

import com.aug.ecommerce.application.event.OrdenAPagarEvent;
import com.aug.ecommerce.application.event.PagoConfirmadoEvent;
import com.aug.ecommerce.application.service.PagoService;
import com.aug.ecommerce.infrastructure.queue.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("rabbit")
public class PagoRabbitListener {

    private final PagoService pagoService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "orden.pago.solicitar.v1.queue")
    public void realizarPagoDeOrden(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.pago.solicitar".equals(wrapper.getEventType())) {
                OrdenAPagarEvent event = objectMapper.convertValue(wrapper.getData(), OrdenAPagarEvent.class);
                log.debug("---> Entrando al RabbitListener PagoRabbitListener - realizarPagoDeOrden {}", event.ordenId());
                pagoService.realizarPago(event);
            } else
                log.warn("### onOrdenListaParaPago -> Evento de orden no reconocido: {}", wrapper.getEventType());
        } catch (Exception e) {
            log.error("Error en PagoRabbitListener", e);
        }
    }
}
