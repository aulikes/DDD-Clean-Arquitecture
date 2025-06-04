package com.aug.ecommerce.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaBeansConfig {

    @Bean(name = "producerKafka")
    public AppProperties.Kafka.Producer producerKafka(AppProperties properties) {
        return properties.getKafka().getProducer();
    }

    @Bean(name = "consumerKafka")
    public AppProperties.Kafka.Consumer consumerKafka(AppProperties properties) {
        return properties.getKafka().getConsumer();
    }
}

