
package com.aug.ecommerce.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("kafka")
public class KafkaTopicConfig {

    private final AppProperties.Kafka.Producer producer;

    public KafkaTopicConfig(AppProperties appProperties) {
        this.producer = appProperties.getKafka().getProducer();
    }

    @Bean
    public NewTopic clienteValidadoTopic() {
        return TopicBuilder.name(producer.getClienteValidadoTopic()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic clienteNoValidadoTopic() {
        return TopicBuilder.name(producer.getClienteNoValidadoTopic()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic envioPreparadoTopic() {
        return TopicBuilder.name(producer.getEnvioPreparadoTopic()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic inventarioValidadoTopic() {
        return TopicBuilder.name(producer.getInventarioValidadoTopic()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic inventarioNoValidadoTopic() {
        return TopicBuilder.name(producer.getInventarioNoValidadoTopic()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic ordenCreadaTopic() {
        return TopicBuilder.name(producer.getOrdenCreadaTopic()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic ordenPreparadaPagoTopic() {
        return TopicBuilder.name(producer.getOrdenPreparadaPagoTopic()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic ordenPagada() {
        return TopicBuilder.name(producer.getOrdenPagadaTopic()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic pagoRealizadoTopic() {
        return TopicBuilder.name(producer.getPagoRealizadoTopic()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic productoCreadoTopic() {
        return TopicBuilder.name(producer.getProductoCreadoTopic()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic productoValidadoTopic() {
        return TopicBuilder.name(producer.getProductoValidadoTopic()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic productoNoValidadoTopic() {
        return TopicBuilder.name(producer.getProductoNoValidadoTopic()).partitions(1).replicas(1).build();
    }


}
