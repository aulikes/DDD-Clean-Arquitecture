
package com.aug.ecommerce.infrastructure.listener.kafkalistener;

import com.aug.ecommerce.application.event.*;
import com.aug.ecommerce.application.service.OrdenValidacionService;
import com.aug.ecommerce.application.service.ValidacionCrearOrden;
import com.aug.ecommerce.infrastructure.queue.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("kafka")
public class OrdenKafkaListener {

    private final ObjectMapper objectMapper;
    private final OrdenValidacionService ordenValidacionService;

    public OrdenKafkaListener(ObjectMapper objectMapper, OrdenValidacionService ordenValidacionService) {
        this.objectMapper = objectMapper;
        this.ordenValidacionService = ordenValidacionService;
    }

    @KafkaListener(topics = "#{@producerKafka.clienteValidadoTopic}",
            groupId = "#{@consumerKafka.clienteOrdenValidadoGroupId}")
    public void clienteValidado(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("cliente.orden.valido".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), ClienteValidadoEvent.class);
                ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.CLIENTE);
            } else
                log.warn("### clienteValidado -> Evento de cliente no reconocido: {}", wrapper.eventType());
        } catch (Exception e) {
            log.error("Error procesando evento de cliente", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.clienteNoValidadoTopic}",
            groupId = "#{@consumerKafka.clienteOrdenNoValidadoGroupId}")
    public void clienteNoValidado(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("cliente.orden.no-valido".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), ClienteNoValidadoEvent.class);
                ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.CLIENTE);
            } else
                log.warn("### clienteNoValidado -> Evento de cliente no reconocido: {}", wrapper.eventType());
        } catch (Exception e) {
            log.error("Error procesando evento de cliente", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.productoValidadoTopic}",
            groupId = "#{@consumerKafka.productoOrdenValidadoGroupId}")
    public void productoValidado(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("producto.orden.valido".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), ProductoValidadoEvent.class);
                ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.PRODUCTO);
            } else
                log.warn("Evento de producto no reconocido: {}", wrapper.eventType());
        } catch (Exception e) {
            log.error("Error procesando evento de producto", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.productoNoValidadoTopic}",
            groupId = "#{@consumerKafka.productoOrdenNoValidadoGroupId}")
    public void productoNoValidado(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("producto.orden.no-valido".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), ProductoNoValidadoEvent.class);
                ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.PRODUCTO);
            } else
                log.warn("Evento de producto no reconocido: {}", wrapper.eventType());
        } catch (Exception e) {
            log.error("Error procesando evento de producto", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.inventarioValidadoTopic}",
            groupId = "#{@consumerKafka.inventarioOrdenValidadoGroupId}")
    public void inventarioDisponible(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("inventario.orden.disponible".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), InventarioValidadoEvent.class);
                ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.STOCK);
            } else
                log.warn("Evento de inventario no reconocido: {}", wrapper.eventType());
        } catch (Exception e) {
            log.error("Error procesando evento de inventario", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.inventarioNoValidadoTopic}",
            groupId = "#{@consumerKafka.inventarioOrdenNoValidadoGroupId}")
    public void inventarioNoDisponible(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("inventario.orden.no-disponible".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), InventarioNoValidadoEvent.class);
                ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.STOCK);
            } else
                log.warn("Evento de inventario no reconocido: {}", wrapper.eventType());
        } catch (Exception e) {
            log.error("Error procesando evento de inventario", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.pagoRealizadoTopic}",
            groupId = "#{@consumerKafka.pagoOrdenValidadoGroupId}")
    public void pagoRealizado(String payload) {
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

    @KafkaListener(topics = "#{@producerKafka.envioPreparadoTopic}",
            groupId = "#{@consumerKafka.envioOrdenPreparadoGroupId}")
    public void envioPreparado(String payload) {
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
