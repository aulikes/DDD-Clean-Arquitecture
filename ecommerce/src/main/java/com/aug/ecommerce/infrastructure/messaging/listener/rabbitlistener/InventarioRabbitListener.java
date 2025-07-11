package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.commands.CrearInventarioCommand;
import com.aug.ecommerce.application.events.*;
import com.aug.ecommerce.application.services.InventarioService;
import com.aug.ecommerce.application.services.InventarioValidacionService;
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
public class InventarioRabbitListener {

    private final InventarioService inventarioService;
    private final InventarioValidacionService inventarioValidacionService;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    public InventarioRabbitListener(InventarioService inventarioService,
                                    InventarioValidacionService inventarioValidacionService,
                                    ObjectMapper objectMapper, AppProperties appProperties) {
        this.inventarioService = inventarioService;
        this.inventarioValidacionService = inventarioValidacionService;
        this.objectMapper = objectMapper;
        this.appProperties = appProperties;
    }

    @RabbitListener(queues = "orden.inventario.validar.v1.queue")
    public void validarInventario(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.multicast.creada".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), OrdenCreadaEvent.class);
                log.debug("---> Entrando a InventarioRabbitListener - validarInventario, orden: {}", event.ordenId());
                inventarioValidacionService.validarInventarioCreacionOrden(event.ordenId(), event.items());
            } else
                log.warn("### validarInventario -> Evento de orden no reconocido: {}", wrapper.eventType());

        } catch (Exception e) {
            log.error("Error en InventarioRabbitListener", e);
        }
    }

    @RabbitListener(queues = "producto.inventario.crear.v1.queue")
    public void crearInventario(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("producto.inventario.crear".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), ProductoCreadoEvent.class);
                log.debug("---> Entrando a InventarioRabbitListener - crearInventario, producto: {}", event.productoId());
                inventarioService.crearInvenario(new CrearInventarioCommand(event.productoId(), event.cantidad()));
            } else
                log.warn("### crearInventario -> Evento de producto no reconocido: {}", wrapper.eventType());

        } catch (Exception e) {
            log.error("Error en InventarioRabbitListener", e);
        }
    }
}
