package com.aug.ecommerce.infrastructure.init;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
@Slf4j
public class AppStartupFinalListener {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitUsr;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPsw;

    private final CategoriaInitializer categoriaInitializer;
    private final ClienteInitializer clienteInitializer;
    private final ProductoInitializer productoInitializer;
    private final OrdenInitializer ordenInitializer;
    private final PagoInitializer pagoInitializer;
    private final Environment env;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("[AppStartupFinalListener] La aplicación está completamente lista.");

        startApplicationEvent();
        categoriaInitializer.run();
        clienteInitializer.run();
        productoInitializer.run();
        ordenInitializer.run();
        pagoInitializer.run();
    }

    private void startApplicationEvent() {
        String profile = String.join(",", env.getActiveProfiles()).toLowerCase();
        log.info("[AppStartupFinalListener] Esperando readiness de infraestructura para perfil: {}", profile);

        try {
            if (profile.contains("kafka")) {
                esperarKafkaListo();
            } else if (profile.contains("rabbit")) {
                esperarRabbitListo();
            }
        } catch (Exception e) {
            log.error("Error al esperar infraestructura: {}", e.getMessage());
            return;
        }

        log.info("[AppStartupFinalListener] Infraestructura lista. Ejecutando carga inicial...");
    }

    private void esperarKafkaListo() throws Exception {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        try (AdminClient client = AdminClient.create(props)) {
            int intentos = 0;
            while (intentos < 60) {
                try {
                    client.listTopics().names().get(1, TimeUnit.SECONDS);
                    log.info("Kafka está listo");
                    return;
                } catch (Exception e) {
                    Thread.sleep(1000);
                    intentos++;
                }
            }
            throw new IllegalStateException("Kafka no respondió en el tiempo esperado");
        }
    }

    private void esperarRabbitListo() throws Exception {
        int intentos = 0;
        while (intentos < 60) {
            try {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(rabbitHost);
                factory.setPort(rabbitPort);
                factory.setUsername(rabbitUsr);
                factory.setPassword(rabbitPsw);
                factory.setConnectionTimeout(2000);

                try (Connection connection = factory.newConnection()) {
                    log.info("RabbitMQ está listo");
                    return;
                }
            } catch (Exception e) {
                Thread.sleep(1000);
                intentos++;
            }
        }
        throw new IllegalStateException("RabbitMQ no respondió en el tiempo esperado");
    }
}
