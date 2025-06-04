package com.aug.ecommerce.infrastructure.listener.rabbitlistener;

import com.aug.ecommerce.application.event.*;
import com.aug.ecommerce.application.service.OrdenValidacionService;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.infrastructure.listener.eventlistener.ValidacionCrearOrden;
import com.aug.ecommerce.infrastructure.queue.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Listener de eventos externos relacionados con la Orden.
 * Escucha eventos provenientes de colas específicas de cliente, producto, inventario, pago y envío.
 */
@Component
@Slf4j
@Profile("rabbit")
public class OrdenRabbitListener {

    private final ObjectMapper objectMapper;
    private final OrdenValidacionService ordenValidacionService;
    private final AppProperties appProperties;

    public OrdenRabbitListener(ObjectMapper objectMapper, OrdenValidacionService ordenValidacionService,
                               AppProperties appProperties) {
        this.objectMapper = objectMapper;
        this.ordenValidacionService = ordenValidacionService;
        this.appProperties = appProperties;
    }

    @RabbitListener(queues = "cliente.orden.validado.v1.queue")
    public void recibirDesdeClienteQueue(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            switch (wrapper.getEventType()) {
                case "cliente.orden.valido" -> {
                    var event = objectMapper.convertValue(wrapper.getData(), ClienteValidadoEvent.class);
                    ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.CLIENTE);
                }
                case "cliente.orden.no-valido" -> {
                    var event = objectMapper.convertValue(wrapper.getData(), ClienteNoValidadoEvent.class);
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
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            switch (wrapper.getEventType()) {
                case "producto.orden.valido" -> {
                    var event = objectMapper.convertValue(wrapper.getData(), ProductoValidadoEvent.class);
                    ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.PRODUCTO);
                }
                case "producto.orden.no-valido" -> {
                    var event = objectMapper.convertValue(wrapper.getData(), ProductoNoValidadoEvent.class);
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
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            switch (wrapper.getEventType()) {
                case "inventario.orden.disponible" -> {
                    var event = objectMapper.convertValue(wrapper.getData(), InventarioValidadoEvent.class);
                    ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.STOCK);
                }
                case "inventario.orden.no-disponible" -> {
                    var event = objectMapper.convertValue(wrapper.getData(), InventarioNoValidadoEvent.class);
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
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("pago.orden.confirmado".equals(wrapper.getEventType())) {
                var event = objectMapper.convertValue(wrapper.getData(), PagoConfirmadoEvent.class);
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
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("envio.orden.preparado".equals(wrapper.getEventType())) {
                var event = objectMapper.convertValue(wrapper.getData(), EnvioPreparadoEvent.class);
                ordenValidacionService.gestionarInformacionEnvio(event);
            } else {
                log.warn("Evento de envío no reconocido: {}", wrapper.getEventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de envío", e);
        }
    }
}
