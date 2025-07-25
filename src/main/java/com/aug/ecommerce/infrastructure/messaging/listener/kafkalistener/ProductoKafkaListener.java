
package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.services.ProductoValidacionService;
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
public class ProductoKafkaListener {

    private final ProductoValidacionService productoValidacionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "#{@producerKafka.ordenCreadaTopic}",
            groupId = "#{@consumerKafka.ordenProductoValidarGroupId}",
            containerFactory = "kafkaListenerContainerFactory" // Esto debe estar definido en KafkaConfig
    )
    public void onMessage(ConsumerRecord<String, IntegrationEventWrapper<OrdenCreadaEvent>> payload) {
        try {
            IntegrationEventWrapper<OrdenCreadaEvent> wrapper = payload.value();
            OrdenCreadaEvent event = objectMapper.convertValue(wrapper.data(), OrdenCreadaEvent.class);
            productoValidacionService.validarProductoCreacionOrden(event.ordenId(), event.items());
        } catch (Exception e) {
            log.error("Error en ProductoKafkaListener", e);
        }
    }
}
