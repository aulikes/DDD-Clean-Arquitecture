package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.services.ProductoValidacionService;
import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("rabbit")
public class ProductoRabbitListener {

    private final ProductoValidacionService productoValidacionService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "orden.producto.validar.v1.queue")
    public void validarProductoCreacionOrden(String payload) { //Valida si el producto existe
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.multicast.creada".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), OrdenCreadaEvent.class);
                productoValidacionService.validarProductoCreacionOrden(event.ordenId(), event.items());
            } else
                log.warn("### validarProductoCreacionOrden -> Evento de producto no reconocido: {}", wrapper.eventType());
        } catch (Exception e) {
            log.error("Error en ProductoRabbitListener", e);
        }
    }
}

