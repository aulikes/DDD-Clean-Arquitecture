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
     * Durable = true: persiste entre reinicios
     * AutoDelete = false: no se elimina cuando queda sin colas
     */
    @Bean
    public TopicExchange eventTopicExchange() {
        return new TopicExchange(eventRabbitMQ.getExchangeTopic(), true, false);
    }

    /**
     * Exchange intermedio de tipo 'fanout' que replica el mensaje hacia todas las colas vinculadas.
     * Este exchange permite que múltiples microservicios escuchen el mismo evento.
     */
    @Bean
    public FanoutExchange ordenFanoutExchange() {
        return new FanoutExchange(eventRabbitMQ.getExchangeFanout(), true, false);
    }

    /**
     * Bridge de Topic Exchange a Fanout Exchange para propagar el mensaje.
     */
    @Bean
    public Binding ordenMulticastFanoutBridgeBinding(
            TopicExchange eventTopicExchange, FanoutExchange ordenFanoutExchange) {
        return BindingBuilder
                .bind(ordenFanoutExchange)
                .to(eventTopicExchange)
                .with("orden.multicast.creada.v1");
    }

    /**=========================================================
     * Colas específicas que escuchan fanout de orden.creada.v1
     * =========================================================*/

    // Cliente -> Orden, para OBTENER la validación de Cliente cuando se va a crear una nueva orden
    @Bean
    public Queue ordenClienteValidarQueue() {
        return new Queue("orden.cliente.validar.v1.queue", true, false, false);
    }

    @Bean
    public Binding ordenClienteBinding(Queue ordenClienteValidarQueue, FanoutExchange ordenFanoutExchange) {
        return BindingBuilder.bind(ordenClienteValidarQueue).to(ordenFanoutExchange);
    }

    // Producto -> Orden, para OBTENER la validación de Producto cuando se va a crear una nueva orden
    @Bean
    public Queue ordenProductoValidarQueue() {
        return new Queue("orden.producto.validar.v1.queue", true, false, false);
    }

    @Bean
    public Binding ordenProductoBinding(Queue ordenProductoValidarQueue, FanoutExchange ordenFanoutExchange) {
        return BindingBuilder.bind(ordenProductoValidarQueue).to(ordenFanoutExchange);
    }

    // Inventario -> Orden, para OBTENER la validación de Inventario cuando se va a crear una nueva orden
    @Bean
    public Queue ordenInventarioValidarQueue() {
        return new Queue("orden.inventario.validar.v1.queue", true, false, false);
    }

    @Bean
    public Binding ordenStockBinding(Queue ordenInventarioValidarQueue, FanoutExchange ordenFanoutExchange) {
        return BindingBuilder.bind(ordenInventarioValidarQueue).to(ordenFanoutExchange);
    }

    /**=========================================================
     * Colas para otros dominios que escuchan del TopicExchange
     * =========================================================*/

    // =======================
    // Cola y binding - ORDEN
    // =======================
    @Bean
    public Queue ordenPagoSolicitarQueue() {
        return new Queue("orden.pago.solicitar.v1.queue", true, false, false);
    }

    @Bean
    public Binding ordenPagoSolicitarBinding(Queue ordenPagoSolicitarQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(ordenPagoSolicitarQueue).to(eventTopicExchange).with("orden.pago.#.v1");
    }

    @Bean
    public Queue ordenEnvioPrepararQueue() {
        return new Queue("orden.envio.preparar.v1.queue", true, false, false);
    }

    @Bean
    public Binding ordenEnvioPrepararBinding(Queue ordenEnvioPrepararQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(ordenEnvioPrepararQueue).to(eventTopicExchange).with("orden.envio.#.v1");
    }

    // =======================
    // Cola y binding - PAGO
    // =======================
    @Bean
    public Queue pagoOrdenValidadoQueue() {
        return new Queue("pago.orden.validado.v1.queue", true, false, false);
    }

    @Bean
    public Binding pagoOrdenValidadoBinding(Queue pagoOrdenValidadoQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(pagoOrdenValidadoQueue).to(eventTopicExchange).with("pago.orden.#.v1");
    }

    // =======================
    // Cola y binding - CLIENTE
    // =======================
    @Bean
    public Queue clienteOrdenValidadoQueue() {
        return new Queue("cliente.orden.validado.v1.queue", true, false, false);
    }

    @Bean
    public Binding clienteOrdenValidadoBinding(Queue clienteOrdenValidadoQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(clienteOrdenValidadoQueue).to(eventTopicExchange).with("cliente.orden.#.v1");
    }

    // =======================
    // Cola y binding - PRODUCTO
    // =======================
    @Bean
    public Queue productoInventarioCrearQueue() {
        return new Queue("producto.inventario.crear.v1.queue", true, false, false);
    }

    @Bean
    public Binding productoInventarioCrearBinding(Queue productoInventarioCrearQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(productoInventarioCrearQueue).to(eventTopicExchange)
                .with("producto.inventario.#.v1");
    }

    @Bean
    public Queue productoOrdenValidadoQueue() {
        return new Queue("producto.orden.validado.v1.queue", true, false, false);
    }

    @Bean
    public Binding productoOrdenValidadoBinding(Queue productoOrdenValidadoQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(productoOrdenValidadoQueue).to(eventTopicExchange)
                .with("producto.orden.#.v1");
    }

    // =======================
    // Cola y binding - INVENTARIO
    // =======================
    @Bean
    public Queue inventarioOrdenValidadoQueue() {
        return new Queue("inventario.orden.validado.v1.queue", true, false, false);
    }

    @Bean
    public Binding inventarioOrdenValidadoBinding(Queue inventarioOrdenValidadoQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(inventarioOrdenValidadoQueue).to(eventTopicExchange).with("inventario.orden.#.v1");
    }

    // =======================
    // Cola y binding - ENVIO
    // =======================
    @Bean
    public Queue envioOrdenPreparadoQueue() {
        return new Queue("envio.orden.preparado.v1.queue", true, false, false);
    }

    @Bean
    public Binding envioBinding(Queue envioOrdenPreparadoQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(envioOrdenPreparadoQueue).to(eventTopicExchange).with("envio.orden.#.v1");
    }
}
