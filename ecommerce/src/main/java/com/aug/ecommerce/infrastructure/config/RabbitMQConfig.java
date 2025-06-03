package com.aug.ecommerce.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para todos los contextos del sistema.
 * Define un exchange único de tipo topic y colas por cada contexto funcional.
 */
@Configuration
public class RabbitMQConfig {

    private final AppProperties.EventRabbitMQ eventRabbitMQ;

    public RabbitMQConfig(AppProperties appProperties) {
        this.eventRabbitMQ = appProperties.getEventRabbitMQ();
    }

    // =======================
    // Exchange principal
    // =======================

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(eventRabbitMQ.getExchange());
    }

    // =======================
    // Cola y binding - ORDEN
    // =======================

    @Bean
    public Queue ordenQueue() {
        return new Queue("orden-events-queue");
    }

    @Bean
    public Binding ordenBinding(Queue ordenQueue, TopicExchange eventExchange) {
        return BindingBuilder
                .bind(ordenQueue)
                .to(eventExchange)
                .with("orden.#.v1");
    }

    // =======================
    // Cola y binding - PAGO
    // =======================

    @Bean
    public Queue pagoQueue() {
        return new Queue("pago-events-queue");
    }

    @Bean
    public Binding pagoBinding(Queue pagoQueue, TopicExchange eventExchange) {
        return BindingBuilder
                .bind(pagoQueue)
                .to(eventExchange)
                .with("pago.#.v1");
    }

    // =======================
    // Cola y binding - CLIENTE
    // =======================

    @Bean
    public Queue clienteQueue() {
        return new Queue("cliente-events-queue");
    }

    @Bean
    public Binding clienteBinding(Queue clienteQueue, TopicExchange eventExchange) {
        return BindingBuilder
                .bind(clienteQueue)
                .to(eventExchange)
                .with("cliente.#.v1");
    }

    // =======================
    // Cola y binding - PRODUCTO
    // =======================

    @Bean
    public Queue productoQueue() {
        return new Queue("producto-events-queue");
    }

    @Bean
    public Binding productoBinding(Queue productoQueue, TopicExchange eventExchange) {
        return BindingBuilder
                .bind(productoQueue)
                .to(eventExchange)
                .with("producto.#.v1");
    }

    // =======================
    // Cola y binding - INVENTARIO
    // =======================

    @Bean
    public Queue inventarioQueue() {
        return new Queue("inventario-events-queue");
    }

    @Bean
    public Binding inventarioBinding(Queue inventarioQueue, TopicExchange eventExchange) {
        return BindingBuilder
                .bind(inventarioQueue)
                .to(eventExchange)
                .with("inventario.#.v1");
    }

    // =======================
    // Cola y binding - ENVIO
    // =======================

    @Bean
    public Queue envioQueue() {
        return new Queue("envio-events-queue");
    }

    @Bean
    public Binding envioBinding(Queue envioQueue, TopicExchange eventExchange) {
        return BindingBuilder
                .bind(envioQueue)
                .to(eventExchange)
                .with("envio.#.v1");
    }
}
