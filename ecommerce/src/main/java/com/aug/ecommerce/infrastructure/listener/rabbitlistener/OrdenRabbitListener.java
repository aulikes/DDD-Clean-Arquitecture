package com.aug.ecommerce.infrastructure.listener.rabbitlistener;

import com.aug.ecommerce.application.event.*;
import com.aug.ecommerce.application.service.OrdenValidacionService;
import com.aug.ecommerce.infrastructure.listener.eventlistener.ValidacionCrearOrden;
import com.aug.ecommerce.infrastructure.queue.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Listener de eventos externos relacionados con la Orden.
 * Escucha eventos provenientes de colas específicas de cliente, producto, inventario, pago y envío.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("rabbit")
public class OrdenRabbitListener {

    private final ObjectMapper objectMapper;
    private final OrdenValidacionService ordenValidacionService;

    @RabbitListener(queues = "cliente.orden.validado.v1.queue")
    public void recibirDesdeClienteQueue(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            switch (wrapper.getEventType()) {
                case "cliente.orden.valido" -> {
                    ClienteValidoEvent event = objectMapper.convertValue(wrapper.getData(), ClienteValidoEvent.class);
                    ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.CLIENTE);
                }
                case "cliente.orden.no-valido" -> {
                    ClienteNoValidoEvent event = objectMapper.convertValue(wrapper.getData(), ClienteNoValidoEvent.class);
                    ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.CLIENTE);
                }
                default -> log.warn("Evento de cliente no reconocido: {}", wrapper.getEventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de cliente", e);
        }
    }

    @RabbitListener(queues = "producto.orden.validado.v1.queue")// CAMBIAR
    public void recibirDesdeProductoQueue(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            switch (wrapper.getEventType()) {
                case "producto.orden.valido" -> {
                    ProductoValidoEvent event = objectMapper.convertValue(wrapper.getData(), ProductoValidoEvent.class);
                    ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.PRODUCTO);
                }
                case "producto.orden.no-valido" -> {
                    ProductoNoValidoEvent event = objectMapper.convertValue(wrapper.getData(), ProductoNoValidoEvent.class);
                    ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.PRODUCTO);
                }
                default -> log.warn("Evento de producto no reconocido: {}", wrapper.getEventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de producto", e);
        }
    }

    @RabbitListener(queues = "inventario.orden.validado.v1.queue")
    public void recibirDesdeInventarioQueue(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            switch (wrapper.getEventType()) {
                case "inventario.orden.disponible" -> {
                    InventarioDisponibleEvent event = objectMapper.convertValue(wrapper.getData(), InventarioDisponibleEvent.class);
                    ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.STOCK);
                }
                case "inventario.orden.no-disponible" -> {
                    InventarioNoDisponibleEvent event = objectMapper.convertValue(wrapper.getData(), InventarioNoDisponibleEvent.class);
                    ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.STOCK);
                }
                default -> log.warn("Evento de inventario no reconocido: {}", wrapper.getEventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de inventario", e);
        }
    }

    @RabbitListener(queues = "pago.orden.validado.v1.queue")
    public void recibirDesdePagoQueue(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("pago.orden.confirmado".equals(wrapper.getEventType())) {
                PagoConfirmadoEvent event = objectMapper.convertValue(wrapper.getData(), PagoConfirmadoEvent.class);
                ordenValidacionService.gestionarInformacionPago(event);
            } else {
                log.warn("Evento de pago no reconocido: {}", wrapper.getEventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de pago", e);
        }
    }

    @RabbitListener(queues = "envio.orden.preparado.v1.queue")
    public void recibirDesdeEnvioQueue(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("envio.orden.preparado".equals(wrapper.getEventType())) {
                EnvioPreparadoEvent event = objectMapper.convertValue(wrapper.getData(), EnvioPreparadoEvent.class);
                ordenValidacionService.gestionarInformacionEnvio(event);
            } else {
                log.warn("Evento de envío no reconocido: {}", wrapper.getEventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de envío", e);
        }
    }
}
