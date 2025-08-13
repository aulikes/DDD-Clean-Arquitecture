package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.events.*;
import com.aug.ecommerce.application.services.OrdenValidacionService;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.application.services.ValidacionCrearOrden;
import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
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
@Slf4j
@RequiredArgsConstructor
@Profile("rabbit")
public class OrdenRabbitListener {

    private final ObjectMapper objectMapper;
    private final OrdenValidacionService ordenValidacionService;

    @RabbitListener(queues = "cliente.orden.validado.v1.queue")
    public void recibirDesdeClienteQueue(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            switch (wrapper.eventType()) {
                case "cliente.orden.valido" -> {
                    var event = objectMapper.convertValue(wrapper.data(), ClienteValidadoEvent.class);
                    ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.CLIENTE);
                }
                case "cliente.orden.no-valido" -> {
                    var event = objectMapper.convertValue(wrapper.data(), ClienteNoValidadoEvent.class);
                    ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.CLIENTE);
                }
                default -> log.warn("Evento de cliente no reconocido: {}", wrapper.eventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de cliente", e);
        }
    }

    @RabbitListener(queues = "producto.orden.validado.v1.queue")// CAMBIAR
    public void recibirDesdeProductoQueue(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            switch (wrapper.eventType()) {
                case "producto.orden.valido" -> {
                    var event = objectMapper.convertValue(wrapper.data(), ProductoValidadoEvent.class);
                    ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.PRODUCTO);
                }
                case "producto.orden.no-valido" -> {
                    var event = objectMapper.convertValue(wrapper.data(), ProductoNoValidadoEvent.class);
                    ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.PRODUCTO);
                }
                default -> log.warn("Evento de producto no reconocido: {}", wrapper.eventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de producto", e);
        }
    }

    @RabbitListener(queues = "inventario.orden.validado.v1.queue")
    public void recibirDesdeInventarioQueue(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            switch (wrapper.eventType()) {
                case "inventario.orden.disponible" -> {
                    var event = objectMapper.convertValue(wrapper.data(), InventarioValidadoEvent.class);
                    ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.STOCK);
                }
                case "inventario.orden.no-disponible" -> {
                    var event = objectMapper.convertValue(wrapper.data(), InventarioNoValidadoEvent.class);
                    ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.STOCK);
                }
                default -> log.warn("Evento de inventario no reconocido: {}", wrapper.eventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de inventario", e);
        }
    }

    @RabbitListener(queues = "pago.orden.validado.v1.queue")
    public void recibirDesdePagoQueue(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("pago.orden.confirmado".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), PagoConfirmadoEvent.class);
                ordenValidacionService.gestionarInformacionPago(event);
            } else {
                log.warn("Evento de pago no reconocido: {}", wrapper.eventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de pago", e);
        }
    }

    @RabbitListener(queues = "envio.orden.preparado.v1.queue")
    public void recibirDesdeEnvioQueue(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("envio.orden.preparado".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), EnvioPreparadoEvent.class);
                ordenValidacionService.gestionarInformacionEnvio(event);
            } else {
                log.warn("Evento de envío no reconocido: {}", wrapper.eventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de envío", e);
        }
    }
}
