package com.aug.ecommerce.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura RabbitMQ para el sistema de eventos distribuidos.
 * Usa una combinación de TopicExchange (para ruteo flexible por tipo de evento)
 * y FanoutExchange (para distribuir el mismo mensaje a múltiples consumidores).
 */
@Configuration
public class RabbitMQConfig {

    private final AppProperties.EventRabbitMQ eventRabbitMQ;

    public RabbitMQConfig(AppProperties appProperties) {
        this.eventRabbitMQ = appProperties.getEventRabbitMQ();
    }

    /**
     * Exchange principal de tipo 'topic', usado para publicar eventos con routing keys como 'orden.creada.vX'.
     */
    @Bean
    public TopicExchange eventTopicExchange() {
        return new TopicExchange(eventRabbitMQ.getExchangeTopic());
    }

    /**
     * Exchange intermedio de tipo 'fanout' que replica el mensaje hacia todas las colas vinculadas.
     * Este exchange permite que múltiples microservicios escuchen el mismo evento.
     */
    @Bean
    public FanoutExchange ordenFanoutExchange() {
        return new FanoutExchange(eventRabbitMQ.getExchangeFanout());
    }

    // =======================
    // Cola y binding - ORDEN
    // =======================
    /**
     * Cola intermediaria que recibe el evento 'orden.creada.vX' desde el exchange 'topic'
     * y lo redirige al exchange 'fanout'.
     */
    @Bean
    public Queue ordenDispatcherQueue() {
        return new Queue("orden-dispatcher-queue", true);
    }

    /**
     * Vincula 'orden-dispatcher-queue' al exchange topic, escuchando el routing key 'orden.creada.v1'.
     */
    @Bean
    public Binding ordenDispatcherBinding(Queue ordenDispatcherQueue, TopicExchange eventExchange) {
        return BindingBuilder
                .bind(ordenDispatcherQueue)
                .to(eventExchange)
                .with("orden.creada.v1");
    }

    /**
     * Conecta 'orden-dispatcher-queue' al fanout exchange para propagar el mensaje.
     */
    @Bean
    public Binding ordenFanoutBridgeBinding(Queue ordenDispatcherQueue, FanoutExchange ordenFanoutExchange) {
        return BindingBuilder
                .bind(ordenDispatcherQueue)
                .to(ordenFanoutExchange);
    }

    /**=========================================================
     * Colas específicas que escuchan fanout de orden.creada.v1
     * =========================================================*/
    // Cliente -> Orden, para OBTENER la validación de Cliente cuando se va a crear una nueva orden
    @Bean
    public Queue ordenClienteQueue() {
        return new Queue("orden-cliente-queue");
    }

    @Bean
    public Binding ordenClienteBinding(Queue ordenClienteQueue, FanoutExchange ordenFanoutExchange) {
        return BindingBuilder.bind(ordenClienteQueue).to(ordenFanoutExchange);
    }

    // Producto -> Orden, para OBTENER la validación de Producto cuando se va a crear una nueva orden
    @Bean
    public Queue ordenProductoQueue() {
        return new Queue("orden-producto-queue");
    }
    @Bean
    public Binding ordenProductoBinding(Queue ordenProductoQueue, FanoutExchange ordenFanoutExchange) {
        return BindingBuilder.bind(ordenProductoQueue).to(ordenFanoutExchange);
    }

    // Inventario -> Orden, para OBTENER la validación de Inventario cuando se va a crear una nueva orden
    @Bean
    public Queue ordenInventarioQueue() {
        return new Queue("orden-inventario-queue");
    }

    @Bean
    public Binding ordenStockBinding(Queue ordenInventarioQueue, FanoutExchange ordenFanoutExchange) {
        return BindingBuilder.bind(ordenInventarioQueue).to(ordenFanoutExchange);
    }


    /**=========================================================
     * Colas para otros dominios que escuchan del TopicExchange
     * =========================================================*/

    // =======================
    // Cola y binding - ORDEN
    // =======================
    @Bean
    public Queue ordenQueue() {
        return new Queue("orden-events-queue");
    }

    @Bean
    public Binding ordenBinding(Queue ordenQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(ordenQueue).to(eventTopicExchange).with("orden.#.v1");
    }

    // =======================
    // Cola y binding - PAGO
    // =======================
    @Bean
    public Queue pagoQueue() {
        return new Queue("pago-events-queue");
    }

    @Bean
    public Binding pagoBinding(Queue pagoQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(pagoQueue).to(eventTopicExchange).with("pago.#.v1");
    }

    // =======================
    // Cola y binding - CLIENTE
    // =======================
    @Bean
    public Queue clienteQueue() {
        return new Queue("cliente-events-queue");
    }

    @Bean
    public Binding clienteBinding(Queue clienteQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(clienteQueue).to(eventTopicExchange).with("cliente.#.v1");
    }

    // =======================
    // Cola y binding - PRODUCTO
    // =======================
    @Bean
    public Queue productoQueue() {
        return new Queue("producto-events-queue");
    }

    @Bean
    public Binding productoBinding(Queue productoQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(productoQueue).to(eventTopicExchange)
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
    public Binding inventarioBinding(Queue inventarioQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(inventarioQueue).to(eventTopicExchange).with("inventario.#.v1");
    }

    // =======================
    // Cola y binding - ENVIO
    // =======================
    @Bean
    public Queue envioQueue() {
        return new Queue("envio-events-queue");
    }

    @Bean
    public Binding envioBinding(Queue envioQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(envioQueue).to(eventTopicExchange).with("envio.#.v1");
    }
}
