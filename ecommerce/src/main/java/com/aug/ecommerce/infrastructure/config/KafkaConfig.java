
package com.aug.ecommerce.infrastructure.config;

import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableKafka
@Profile("kafka")
public class KafkaConfig {

    private final AppProperties properties;

    @Bean(name = "producerKafka")
    public AppProperties.Kafka.Producer producerKafka(AppProperties properties) {
        return properties.getKafka().getProducer();
    }

    @Bean(name = "consumerKafka")
    public AppProperties.Kafka.Consumer consumerKafka(AppProperties properties) {
        return properties.getKafka().getConsumer();
    }

    public ProducerFactory<String, IntegrationEventWrapper<?>> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getKafka().getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, IntegrationEventWrapper<?>> kafkaTemplate() { // Este es el Template que usamos para producir los eventos
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, IntegrationEventWrapper<?>> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getKafka().getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getKafka().getOrdenConsumerGroup());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(IntegrationEventWrapper.class, false) // <-- "false" desactiva el uso de headers
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, IntegrationEventWrapper<?>> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, IntegrationEventWrapper<?>>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
