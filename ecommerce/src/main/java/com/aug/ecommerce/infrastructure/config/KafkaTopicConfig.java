
package com.aug.ecommerce.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("kafka")
public class KafkaTopicConfig {

    @Bean
    public NewTopic ordenesTopic() {
        return TopicBuilder.name("ordenes").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic clientesTopic() {
        return TopicBuilder.name("clientes").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic productosTopic() {
        return TopicBuilder.name("productos").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic inventariosTopic() {
        return TopicBuilder.name("inventarios").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic pagosTopic() {
        return TopicBuilder.name("pagos").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic enviosTopic() {
        return TopicBuilder.name("envios").partitions(1).replicas(1).build();
    }
}
