package com.aug.ecommerce.infrastructure.listener.rabbitlistener;
import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.service.ClienteValidacionService;
import com.aug.ecommerce.infrastructure.queue.IntegrationEventWrapper;
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

    @RabbitListener(queues = "orden.cliente.validar.v1.queue")
    public void validarCliente(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.multicast.creada".equals(wrapper.getEventType())) {
                OrdenCreadaEvent event = objectMapper.convertValue(wrapper.getData(), OrdenCreadaEvent.class);
                log.debug("---> Entrando al RabbitListener ClienteRabbitListener - OrdenCreadaEvent {}", event.ordenId());
                clienteValidacionService.validarClienteCreacionOrden(event.ordenId(), event.clienteId());
            } else
                log.warn("### onOrdenListaParaPago -> Evento de orden no reconocido: {}", wrapper.getEventType());

        } catch (Exception e) {
            log.error("Error procesando mensaje en ClienteRabbitListener", e);
        }
    }
}


