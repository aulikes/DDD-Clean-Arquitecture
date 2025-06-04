package com.aug.ecommerce.infrastructure.listener.rabbitlistener;

import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.service.ProductoValidacionService;
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
public class ProductoRabbitListener {

    private final ProductoValidacionService productoValidacionService;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    public ProductoRabbitListener(ProductoValidacionService productoValidacionService, ObjectMapper objectMapper,
                                  AppProperties appProperties) {
        this.productoValidacionService = productoValidacionService;
        this.objectMapper = objectMapper;
        this.appProperties = appProperties;
    }

    @RabbitListener(queues = "orden.producto.validar.v1.queue")
    public void validarProductoCreacionOrden(String payload) { //Valida si el producto existe
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.multicast.creada".equals(wrapper.getEventType())) {
                var event = objectMapper.convertValue(wrapper.getData(), OrdenCreadaEvent.class);
                productoValidacionService.validarProductoCreacionOrden(event.ordenId(), event.items());
            } else
                log.warn("### validarProductoCreacionOrden -> Evento de producto no reconocido: {}", wrapper.getEventType());
        } catch (Exception e) {
            log.error("Error en ProductoRabbitListener", e);
        }
    }
}

