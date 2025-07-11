
package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.commands.CrearInventarioCommand;
import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.events.ProductoCreadoEvent;
import com.aug.ecommerce.application.services.InventarioService;
import com.aug.ecommerce.application.services.InventarioValidacionService;
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
public class InventarioKafkaListener {

    private final InventarioService inventarioService;
    private final InventarioValidacionService inventarioValidacionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "#{@producerKafka.ordenCreadaTopic}",
            groupId = "#{@consumerKafka.ordenInventarioValidarGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void onMessageOrdenCreada(ConsumerRecord<String, IntegrationEventWrapper<OrdenCreadaEvent>> payload) {
        try {
            IntegrationEventWrapper<OrdenCreadaEvent> wrapper = payload.value();
            OrdenCreadaEvent event = objectMapper.convertValue(wrapper.data(), OrdenCreadaEvent.class);
            log.debug("---> Entrando a InventarioKafkaListener - validarInventario, orden: {}", event.ordenId());
            inventarioValidacionService.validarInventarioCreacionOrden(event.ordenId(), event.items());
        } catch (Exception e) {
            log.error("Error en InventarioKafkaListener", e);
        }
    }

    @KafkaListener(topics = "#{@producerKafka.productoCreadoTopic}",
            groupId = "#{@consumerKafka.productoInventarioCrearGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void onMessageProductoCreado(ConsumerRecord<String, IntegrationEventWrapper<ProductoCreadoEvent>> payload) {
        try {
            IntegrationEventWrapper<ProductoCreadoEvent> wrapper = payload.value();
            ProductoCreadoEvent event = objectMapper.convertValue(wrapper.data(), ProductoCreadoEvent.class);
            log.debug("---> Entrando a InventarioKafkaListener - crearInventario, producto: {}", event.productoId());
            inventarioService.crearInvenario(new CrearInventarioCommand(event.productoId(), event.cantidad()));
        } catch (Exception e) {
            log.error("Error en InventarioKafkaListener", e);
        }
    }
}
