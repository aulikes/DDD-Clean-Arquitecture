package com.aug.ecommerce.infrastructure.listener.rabbitlistener;

import com.aug.ecommerce.application.command.CrearInventarioCommand;
import com.aug.ecommerce.application.event.*;
import com.aug.ecommerce.application.service.InventarioService;
import com.aug.ecommerce.application.service.InventarioValidacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventarioRabbitListener {

    private final InventarioService inventarioService;
    private final InventarioValidacionService inventarioValidacionService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "orden-events-queue")
    public void validarInventario(String payload) {
        log.debug("---> Entrando al RabbitListener InventarioRabbitListener - onEvento");
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.creada".equals(wrapper.getEventType())) {
                OrdenCreadaEvent event = objectMapper.readValue(payload, OrdenCreadaEvent.class);
                log.debug("---> Entrando al RabbitListener InventarioRabbitListener - onEvento, orden: {}", event.getOrdenId());
                inventarioValidacionService.validarInventarioCreacionOrden(event.getOrdenId(), event.getItems());
            } else
                log.warn("### validarInventario -> Evento de orden no reconocido: {}", wrapper.getEventType());

        } catch (Exception e) {
            log.error("Error en InventarioRabbitListener", e);
        }
    }

    @RabbitListener(queues = "producto-events-queue")
    public void crearInventario(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("producto.creado".equals(wrapper.getEventType())) {
                ProductoCreadoEvent event = objectMapper.readValue(payload, ProductoCreadoEvent.class);
                log.debug("---> Entrando al RabbitListener InventarioRabbitListener - onEvento, producto: {}", event.productoId());
                inventarioService.crearInvenario(new CrearInventarioCommand(event.productoId(), event.cantidad()));
            } else
                log.warn("### crearInventario -> Evento de producto no reconocido: {}", wrapper.getEventType());

        } catch (Exception e) {
            log.error("Error en InventarioRabbitListener", e);
        }
    }
}
