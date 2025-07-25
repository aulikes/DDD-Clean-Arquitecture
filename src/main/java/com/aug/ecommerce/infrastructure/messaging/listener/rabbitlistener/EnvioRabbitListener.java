package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.events.OrdenPagadaEvent;
import com.aug.ecommerce.application.services.EnvioService;
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
public class EnvioRabbitListener {

    private final EnvioService envioService;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    public EnvioRabbitListener(EnvioService envioService, ObjectMapper objectMapper, AppProperties appProperties) {
        this.envioService = envioService;
        this.objectMapper = objectMapper;
        this.appProperties = appProperties;
    }

    @RabbitListener(queues = "orden.envio.preparar.v1.queue")
    public void prepararEnvio(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.envio.preparar".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), OrdenPagadaEvent.class);
                log.debug("---> Entrando al RabbitListener EnvioRabbitListener - onOrdenPagada {}", event.ordenId());
                envioService.crearEnvio(event.ordenId(), event.direccionEnvio());
            } else
                log.warn("### prepararEnvio -> Evento de orden no reconocido: {}", wrapper.eventType());
        } catch (Exception e) {
            log.error("Error en EnvioRabbitListener", e);
        }
    }
}
