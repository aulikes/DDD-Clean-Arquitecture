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

    /**
     * Cola intermediaria que recibe el evento 'orden.creada.vX' desde el exchange 'topic'
     * y lo redirige al exchange 'fanout'.
     */
    @Bean
    public Queue ordenMulticastDispatcherQueue() {
        return new Queue("orden.multicast.creada.v1.queue", true);
    }

    /**
     * Vincula 'orden-dispatcher-queue' al exchange topic, escuchando el routing key 'orden.creada.v1'.
     */
    @Bean
    public Binding ordenDispatcherBinding(Queue ordenMulticastDispatcherQueue, TopicExchange eventExchange) {
        return BindingBuilder
                .bind(ordenMulticastDispatcherQueue)
                .to(eventExchange)
                .with("orden.multicast.creada.v1");
    }

    /**
     * Conecta 'orden.multicast.creada.v1.queue' al fanout exchange para propagar el mensaje.
     */
    @Bean
    public Binding ordenMulticastFanoutBridgeBinding(Queue ordenMulticastDispatcherQueue, FanoutExchange ordenFanoutExchange) {
        return BindingBuilder
                .bind(ordenMulticastDispatcherQueue)
                .to(ordenFanoutExchange);
    }

    /**=========================================================
     * Colas específicas que escuchan fanout de orden.creada.v1
     * =========================================================*/
    // Cliente -> Orden, para OBTENER la validación de Cliente cuando se va a crear una nueva orden
    @Bean
    public Queue ordenClienteValidarQueue() {
        return new Queue("orden.cliente.validar.v1.queue");
    }

    @Bean
    public Binding ordenClienteBinding(Queue ordenClienteValidarQueue, FanoutExchange ordenFanoutExchange) {
        return BindingBuilder.bind(ordenClienteValidarQueue).to(ordenFanoutExchange);
    }

    // Producto -> Orden, para OBTENER la validación de Producto cuando se va a crear una nueva orden
    @Bean
    public Queue ordenProductoValidarQueue() {
        return new Queue("orden.producto.validar.v1.queue");
    }
    @Bean
    public Binding ordenProductoBinding(Queue ordenProductoValidarQueue, FanoutExchange ordenFanoutExchange) {
        return BindingBuilder.bind(ordenProductoValidarQueue).to(ordenFanoutExchange);
    }

    // Inventario -> Orden, para OBTENER la validación de Inventario cuando se va a crear una nueva orden
    @Bean
    public Queue ordenInventarioValidarQueue() {
        return new Queue("orden.inventario.validar.v1.queue");
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
        return new Queue("orden.pago.solicitar.v1.queue");
    }

    @Bean
    public Binding ordenPagoSolicitarBinding(Queue ordenPagoSolicitarQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(ordenPagoSolicitarQueue).to(eventTopicExchange).with("orden.pago.#.v1");
    }
    @Bean
    public Queue ordenEnvioPrepararQueue() {
        return new Queue("orden.envio.preparar.v1.queue");
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
        return new Queue("pago.orden.validado.v1.queue");
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
        return new Queue("cliente.orden.validado.v1.queue"); //cliente-orden-validate-queue
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
        return new Queue("producto.inventario.crear.v1.queue");
    }

    @Bean
    public Binding productoInventarioCrearBinding(Queue productoInventarioCrearQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(productoInventarioCrearQueue).to(eventTopicExchange)
                .with("producto.inventario.#.v1");
    }

    @Bean
    public Queue productoOrdenValidadoQueue() {
        return new Queue("producto.orden.validado.v1.queue");
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
        return new Queue("inventario.orden.validado.v1.queue");
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
        return new Queue("envio.orden.preparado.v1.queue");
    }

    @Bean
    public Binding envioBinding(Queue envioOrdenPreparadoQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(envioOrdenPreparadoQueue).to(eventTopicExchange).with("envio.orden.#.v1");
    }
}
