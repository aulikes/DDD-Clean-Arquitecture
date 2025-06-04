package com.aug.ecommerce.infrastructure.listener.rabbitlistener;

import com.aug.ecommerce.application.event.EnvioRequestedEvent;
import com.aug.ecommerce.application.event.ProductoCreadoEvent;
import com.aug.ecommerce.application.service.EnvioService;
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
public class EnvioRabbitListener {

    private final EnvioService envioService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "orden.envio.preparar.v1.queue")
    public void prepararEnvio(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.envio.preparar".equals(wrapper.getEventType())) {
                EnvioRequestedEvent event = objectMapper.convertValue(wrapper.getData(), EnvioRequestedEvent.class);
                log.debug("---> Entrando al RabbitListener EnvioRabbitListener - onOrdenPagada {}", event.ordenId());
                envioService.prepararEnvio(event.ordenId(), event.direccionEnvio());
            } else
                log.warn("### onOrdenListaParaPago -> Evento de orden no reconocido: {}", wrapper.getEventType());
        } catch (Exception e) {
            log.error("Error en EnvioRabbitListener", e);
        }
    }
}
