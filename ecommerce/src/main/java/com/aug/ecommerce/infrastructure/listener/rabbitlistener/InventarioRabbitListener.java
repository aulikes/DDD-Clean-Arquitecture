package com.aug.ecommerce.infrastructure.listener.rabbitlistener;

import com.aug.ecommerce.application.command.CrearInventarioCommand;
import com.aug.ecommerce.application.event.*;
import com.aug.ecommerce.application.service.InventarioService;
import com.aug.ecommerce.application.service.InventarioValidacionService;
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
public class InventarioRabbitListener {

    private final InventarioService inventarioService;
    private final InventarioValidacionService inventarioValidacionService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "orden.inventario.validar.v1.queue")
    public void validarInventario(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.multicast.creada".equals(wrapper.getEventType())) {
                OrdenCreadaEvent event = objectMapper.convertValue(wrapper.getData(), OrdenCreadaEvent.class);
                log.debug("---> Entrando al RabbitListener InventarioRabbitListener - onEvento, orden: {}", event.ordenId());
                inventarioValidacionService.validarInventarioCreacionOrden(event.ordenId(), event.items());
            } else
                log.warn("### validarInventario -> Evento de orden no reconocido: {}", wrapper.getEventType());

        } catch (Exception e) {
            log.error("Error en InventarioRabbitListener", e);
        }
    }

    @RabbitListener(queues = "producto.inventario.crear.v1.queue")
    public void crearInventario(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("producto.inventario.crear".equals(wrapper.getEventType())) {
                ProductoCreadoEvent event = objectMapper.convertValue(wrapper.getData(), ProductoCreadoEvent.class);
                log.debug("---> Entrando al RabbitListener InventarioRabbitListener - onEvento, producto: {}", event.productoId());
                inventarioService.crearInvenario(new CrearInventarioCommand(event.productoId(), event.cantidad()));
            } else
                log.warn("### crearInventario -> Evento de producto no reconocido: {}", wrapper.getEventType());

        } catch (Exception e) {
            log.error("Error en InventarioRabbitListener", e);
        }
    }
}
