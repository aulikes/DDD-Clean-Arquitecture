package com.aug.ecommerce.infrastructure.listener.rabbitlistener;
import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.service.ClienteValidacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteRabbitListener {

    private final ClienteValidacionService clienteValidacionService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "orden-events-queue")
    public void validarCliente(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.creada".equals(wrapper.getEventType())) {
                OrdenCreadaEvent event = objectMapper.readValue(payload, OrdenCreadaEvent.class);
                log.debug("---> Entrando al RabbitListener ClienteRabbitListener - OrdenCreadaEvent {}", event.getOrdenId());
                clienteValidacionService.validarClienteCreacionOrden(event.getOrdenId(), event.getClienteId());
            } else
                log.warn("### onOrdenListaParaPago -> Evento de orden no reconocido: {}", wrapper.getEventType());

        } catch (Exception e) {
            log.error("Error procesando mensaje en ClienteRabbitListener", e);
        }
    }
}


