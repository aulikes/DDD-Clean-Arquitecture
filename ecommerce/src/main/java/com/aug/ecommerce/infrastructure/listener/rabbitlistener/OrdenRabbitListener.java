package com.aug.ecommerce.infrastructure.listener.rabbitlistener;

import com.aug.ecommerce.application.event.*;
import com.aug.ecommerce.application.service.OrdenValidacionService;
import com.aug.ecommerce.infrastructure.listener.evenlistener.ValidacionCrearOrden;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener de eventos externos relacionados con la Orden.
 * Escucha eventos provenientes de colas específicas de cliente, producto, inventario, pago y envío.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrdenRabbitListener {

    private final ObjectMapper objectMapper;
    private final OrdenValidacionService ordenValidacionService;

    @RabbitListener(queues = "cliente-events-queue")
    public void recibirDesdeClienteQueue(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            switch (wrapper.getEventType()) {
                case "cliente.valido" -> {
                    ClienteValidoEvent evento = objectMapper.readValue(payload, ClienteValidoEvent.class);
                    ordenValidacionService.registrarValidacionExitosa(evento.ordenId(), ValidacionCrearOrden.CLIENTE);
                }
                case "cliente.no-valido" -> {
                    ClienteNoValidoEvent evento = objectMapper.readValue(payload, ClienteNoValidoEvent.class);
                    ordenValidacionService.registrarValidacionFallida(evento.ordenId(), ValidacionCrearOrden.CLIENTE);
                }
                default -> log.warn("Evento de cliente no reconocido: {}", wrapper.getEventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de cliente", e);
        }
    }

    @RabbitListener(queues = "producto-events-queue")
    public void recibirDesdeProductoQueue(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            switch (wrapper.getEventType()) {
                case "producto.valido" -> {
                    ProductoValidoEvent evento = objectMapper.readValue(payload, ProductoValidoEvent.class);
                    ordenValidacionService.registrarValidacionExitosa(evento.ordenId(), ValidacionCrearOrden.PRODUCTO);
                }
                case "producto.no-valido" -> {
                    ProductoNoValidoEvent evento = objectMapper.readValue(payload, ProductoNoValidoEvent.class);
                    ordenValidacionService.registrarValidacionFallida(evento.ordenId(), ValidacionCrearOrden.PRODUCTO);
                }
                default -> log.warn("Evento de producto no reconocido: {}", wrapper.getEventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de producto", e);
        }
    }

    @RabbitListener(queues = "inventario-events-queue")
    public void recibirDesdeInventarioQueue(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            switch (wrapper.getEventType()) {
                case "inventario.disponible" -> {
                    StockDisponibleEvent evento = objectMapper.readValue(payload, StockDisponibleEvent.class);
                    ordenValidacionService.registrarValidacionExitosa(evento.ordenId(), ValidacionCrearOrden.STOCK);
                }
                case "inventario.no-disponible" -> {
                    StockNoDisponibleEvent evento = objectMapper.readValue(payload, StockNoDisponibleEvent.class);
                    ordenValidacionService.registrarValidacionFallida(evento.ordenId(), ValidacionCrearOrden.STOCK);
                }
                default -> log.warn("Evento de inventario no reconocido: {}", wrapper.getEventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de inventario", e);
        }
    }

    @RabbitListener(queues = "pago-events-queue")
    public void recibirDesdePagoQueue(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("pago.confirmado".equals(wrapper.getEventType())) {
                PagoConfirmadoEvent evento = objectMapper.readValue(payload, PagoConfirmadoEvent.class);
                ordenValidacionService.gestionarInformacionPago(evento);
            } else {
                log.warn("Evento de pago no reconocido: {}", wrapper.getEventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de pago", e);
        }
    }

    @RabbitListener(queues = "envio-events-queue")
    public void recibirDesdeEnvioQueue(String payload) {
        try {
            IntegrationEventWrapper wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("envio.preparado".equals(wrapper.getEventType())) {
                EnvioPreparadoEvent evento = objectMapper.readValue(payload, EnvioPreparadoEvent.class);
                ordenValidacionService.gestionarInformacionEnvio(evento);
            } else {
                log.warn("Evento de envío no reconocido: {}", wrapper.getEventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de envío", e);
        }
    }
}
