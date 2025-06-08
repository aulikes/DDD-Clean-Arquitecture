
package com.aug.ecommerce.infrastructure.listener.kafkalistener;

import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.service.ProductoValidacionService;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.infrastructure.queue.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("kafka")
public class ProductoKafkaListener {

    private final ProductoValidacionService productoValidacionService;
    private final ObjectMapper objectMapper;

    public ProductoKafkaListener(ProductoValidacionService productoValidacionService, ObjectMapper objectMapper, AppProperties appProperties) {
        this.productoValidacionService = productoValidacionService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "#{@producerKafka.ordenCreadaTopic}",
            groupId = "#{@consumerKafka.ordenProductoValidarGroupId}")
    public void validarProductoCreacionOrden(String payload) { //Valida si el producto existe
        try {
            var wrapper = objectMapper.readValue(payload, IntegrationEventWrapper.class);
            if ("orden.multicast.creada".equals(wrapper.eventType())) {
                var event = objectMapper.convertValue(wrapper.data(), OrdenCreadaEvent.class);
                productoValidacionService.validarProductoCreacionOrden(event.ordenId(), event.items());
            } else
                log.warn("### validarProductoCreacionOrden -> Evento de producto no reconocido: {}", wrapper.eventType());
        } catch (Exception e) {
            log.error("Error en ProductoKafkaListener", e);
        }
    }
}
