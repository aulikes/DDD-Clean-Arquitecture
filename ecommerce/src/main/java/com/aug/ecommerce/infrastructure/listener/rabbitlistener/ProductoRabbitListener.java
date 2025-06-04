package com.aug.ecommerce.infrastructure.listener.rabbitlistener;

import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.service.ProductoValidacionService;
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
public class ProductoRabbitListener {

    private final ProductoValidacionService productoValidacionService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "orden.producto.validar.v1.queue")
    public void validarProductoCreacionOrden(String payload) { //Valida si el producto existe
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.multicast.creada".equals(wrapper.getEventType())) {
                OrdenCreadaEvent event = objectMapper.convertValue(wrapper.getData(), OrdenCreadaEvent.class);
                productoValidacionService.validarProductoCreacionOrden(event.ordenId(), event.items());
            } else
                log.warn("### validarProductoCreacionOrden -> Evento de producto no reconocido: {}", wrapper.getEventType());
        } catch (Exception e) {
            log.error("Error en ProductoRabbitListener", e);
        }
    }
}

