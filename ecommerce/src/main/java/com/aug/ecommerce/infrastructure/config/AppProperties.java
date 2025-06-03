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
     * Configuración del RABBIT
     */
    private EventRabbitMQ eventRabbitMQ;

    // -----------------------------------------------
    // Subclases anidadas
    // -----------------------------------------------

    @Data
    public static class EventRabbitMQ {
        private String exchange;
    }
}
