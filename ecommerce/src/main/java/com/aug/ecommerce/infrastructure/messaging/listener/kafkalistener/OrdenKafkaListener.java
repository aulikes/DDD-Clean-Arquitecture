
package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.event.*;
import com.aug.ecommerce.application.service.OrdenValidacionService;
import com.aug.ecommerce.application.service.ValidacionCrearOrden;
import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("kafka")
public class OrdenKafkaListener {

    private final OrdenValidacionService ordenValidacionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "#{@producerKafka.clienteValidadoTopic}",
            groupId = "#{@consumerKafka.clienteOrdenValidadoGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void clienteValidado(ConsumerRecord<String, IntegrationEventWrapper<ClienteValidadoEvent>> payload) {
        try {
            IntegrationEventWrapper<ClienteValidadoEvent> wrapper = payload.value();
            ClienteValidadoEvent event = objectMapper.convertValue(wrapper.data(), ClienteValidadoEvent.class);
            ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.CLIENTE);
        } catch (Exception e) {
            log.error("Error procesando evento de cliente", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.clienteNoValidadoTopic}",
            groupId = "#{@consumerKafka.clienteOrdenNoValidadoGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void clienteNoValidado(ConsumerRecord<String, IntegrationEventWrapper<ClienteNoValidadoEvent>> payload) {
        try {
            IntegrationEventWrapper<ClienteNoValidadoEvent> wrapper = payload.value();
            ClienteNoValidadoEvent event = objectMapper.convertValue(wrapper.data(), ClienteNoValidadoEvent.class);
            ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.CLIENTE);
        } catch (Exception e) {
            log.error("Error procesando evento de cliente", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.productoValidadoTopic}",
            groupId = "#{@consumerKafka.productoOrdenValidadoGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void productoValidado(ConsumerRecord<String, IntegrationEventWrapper<ProductoValidadoEvent>> payload) {
        try {
            IntegrationEventWrapper<ProductoValidadoEvent> wrapper = payload.value();
            ProductoValidadoEvent event = objectMapper.convertValue(wrapper.data(), ProductoValidadoEvent.class);
            ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.PRODUCTO);
        } catch (Exception e) {
            log.error("Error procesando evento de producto", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.productoNoValidadoTopic}",
            groupId = "#{@consumerKafka.productoOrdenNoValidadoGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void productoNoValidado(ConsumerRecord<String, IntegrationEventWrapper<ProductoNoValidadoEvent>> payload) {
        try {
            IntegrationEventWrapper<ProductoNoValidadoEvent> wrapper = payload.value();
            ProductoNoValidadoEvent event = objectMapper.convertValue(wrapper.data(), ProductoNoValidadoEvent.class);
            ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.PRODUCTO);
        } catch (Exception e) {
            log.error("Error procesando evento de producto", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.inventarioValidadoTopic}",
            groupId = "#{@consumerKafka.inventarioOrdenValidadoGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void inventarioDisponible(ConsumerRecord<String, IntegrationEventWrapper<InventarioValidadoEvent>> payload) {
        try {
            IntegrationEventWrapper<InventarioValidadoEvent> wrapper = payload.value();
            InventarioValidadoEvent event = objectMapper.convertValue(wrapper.data(), InventarioValidadoEvent.class);
            ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.STOCK);
        } catch (Exception e) {
            log.error("Error procesando evento de inventario", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.inventarioNoValidadoTopic}",
            groupId = "#{@consumerKafka.inventarioOrdenNoValidadoGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void inventarioNoDisponible(ConsumerRecord<String, IntegrationEventWrapper<InventarioNoValidadoEvent>> payload) {
        try {
            IntegrationEventWrapper<InventarioNoValidadoEvent> wrapper = payload.value();
            InventarioNoValidadoEvent event = objectMapper.convertValue(wrapper.data(), InventarioNoValidadoEvent.class);
            ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.STOCK);
        } catch (Exception e) {
            log.error("Error procesando evento de inventario", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.pagoRealizadoTopic}",
            groupId = "#{@consumerKafka.pagoOrdenValidadoGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void pagoRealizado(ConsumerRecord<String, IntegrationEventWrapper<PagoConfirmadoEvent>> payload) {
        try {
            IntegrationEventWrapper<PagoConfirmadoEvent> wrapper = payload.value();
            PagoConfirmadoEvent event = objectMapper.convertValue(wrapper.data(), PagoConfirmadoEvent.class);
            ordenValidacionService.gestionarInformacionPago(event);
        } catch (Exception e) {
            log.error("Error procesando evento de pago", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.envioPreparadoTopic}",
            groupId = "#{@consumerKafka.envioOrdenPreparadoGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void envioPreparado(ConsumerRecord<String, IntegrationEventWrapper<EnvioPreparadoEvent>> payload) {
        try {
            IntegrationEventWrapper<EnvioPreparadoEvent> wrapper = payload.value();
            EnvioPreparadoEvent event = objectMapper.convertValue(wrapper.data(), EnvioPreparadoEvent.class);
            ordenValidacionService.gestionarInformacionEnvio(event);
        } catch (Exception e) {
            log.error("Error procesando evento de envío", e);
        }
    }
}
