package com.aug.ecommerce.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Mapea todas las propiedades definidas en application.yml bajo el prefijo "app".
 * Centraliza la configuración del sistema para facilitar mantenimiento y ajustes.
 */
@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Configuración de RABBIT
     */
    private EventRabbitMQ eventRabbitMQ;

    /**
     * Configuración de KAFKA
     */
    private Kafka kafka;

    // -----------------------------------------------
    // Subclases anidadas
    // -----------------------------------------------

    @Data
    public static class EventRabbitMQ {
        private String exchangeTopic;
        private String exchangeFanout;
    }

    @Data
    public static class Kafka {
        private String bootstrapServers;
        private String ordenConsumerGroup;
        private Producer producer;
        private Consumer consumer;

        @Data
        public static class Producer {
            private String clienteValidadoTopic;
            private String clienteNoValidadoTopic;
            private String envioPreparadoTopic;
            private String inventarioValidadoTopic;
            private String inventarioNoValidadoTopic;
            private String ordenCreadaTopic;
            private String ordenPreparadaPagoTopic;
            private String ordenPagadaTopic;
            private String pagoRealizadoTopic;
            private String productoCreadoTopic;
            private String productoValidadoTopic;
            private String productoNoValidadoTopic;
        }

        @Data
        public static class Consumer {
            private String ordenClienteValidarGroupId;
            private String ordenInventarioValidarGroupId;
            private String ordenProductoValidarGroupId;
            private String ordenEnvioPrepararGroupId;
            private String productoInventarioCrearGroupId;
            private String clienteOrdenValidadoGroupId;
            private String clienteOrdenNoValidadoGroupId;
            private String productoOrdenValidadoGroupId;
            private String productoOrdenNoValidadoGroupId;
            private String inventarioOrdenValidadoGroupId;
            private String inventarioOrdenNoValidadoGroupId;
            private String pagoOrdenValidadoGroupId;
            private String envioOrdenPreparadoGroupId;
            private String ordenPagoSolicitarGroupId;
        }
    }
}
