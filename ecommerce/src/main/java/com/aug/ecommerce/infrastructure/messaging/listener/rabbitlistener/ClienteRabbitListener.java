package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;
import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.service.ClienteValidacionService;
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
public class ClienteRabbitListener {

    private final ClienteValidacionService clienteValidacionService;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    public ClienteRabbitListener(ClienteValidacionService clienteValidacionService,
                                 ObjectMapper objectMapper, AppProperties appProperties) {
        this.clienteValidacionService = clienteValidacionService;
        this.objectMapper = objectMapper;
        this.appProperties = appProperties;
    }

    @RabbitListener(queues = "orden.cliente.validar.v1.queue")
    public void validarCliente(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.multicast.creada".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), OrdenCreadaEvent.class);
                log.debug("---> Entrando al RabbitListener ClienteRabbitListener - OrdenCreadaEvent {}", event.ordenId());
                clienteValidacionService.validarClienteCreacionOrden(event.ordenId(), event.clienteId());
            } else
                log.warn("### validarCliente -> Evento de orden no reconocido: {}", wrapper.eventType());

        } catch (Exception e) {
            log.error("Error procesando mensaje en ClienteRabbitListener", e);
        }
    }
}


