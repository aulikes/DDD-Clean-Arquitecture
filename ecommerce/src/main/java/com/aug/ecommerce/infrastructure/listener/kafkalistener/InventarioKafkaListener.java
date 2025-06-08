
package com.aug.ecommerce.infrastructure.listener.kafkalistener;

import com.aug.ecommerce.application.command.CrearInventarioCommand;
import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.event.ProductoCreadoEvent;
import com.aug.ecommerce.application.service.InventarioService;
import com.aug.ecommerce.application.service.InventarioValidacionService;
import com.aug.ecommerce.infrastructure.queue.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("kafka")
public class InventarioKafkaListener {

    private final InventarioService inventarioService;
    private final InventarioValidacionService inventarioValidacionService;
    private final ObjectMapper objectMapper;

    public InventarioKafkaListener(InventarioService inventarioService,
                                    InventarioValidacionService inventarioValidacionService,
                                    ObjectMapper objectMapper) {
        this.inventarioService = inventarioService;
        this.inventarioValidacionService = inventarioValidacionService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "#{@producerKafka.ordenCreadaTopic}",
            groupId = "#{@consumerKafka.ordenInventarioValidarGroupId}")
    public void validarInventario(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.multicast.creada".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), OrdenCreadaEvent.class);
                log.debug("---> Entrando a InventarioKafkaListener - validarInventario, orden: {}", event.ordenId());
                inventarioValidacionService.validarInventarioCreacionOrden(event.ordenId(), event.items());
            } else
                log.warn("### validarInventario -> Evento de orden no reconocido: {}", wrapper.eventType());

        } catch (Exception e) {
            log.error("Error en InventarioKafkaListener", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.productoCreadoTopic}",
            groupId = "#{@consumerKafka.productoInventarioCrearGroupId}")
    public void crearInventario(String payload) {
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("producto.inventario.crear".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), ProductoCreadoEvent.class);
                log.debug("---> Entrando a InventarioKafkaListener - crearInventario, producto: {}", event.productoId());
                inventarioService.crearInvenario(new CrearInventarioCommand(event.productoId(), event.cantidad()));
            } else
                log.warn("### crearInventario -> Evento de producto no reconocido: {}", wrapper.eventType());

        } catch (Exception e) {
            log.error("Error en InventarioKafkaListener", e);
        }
    }
}
