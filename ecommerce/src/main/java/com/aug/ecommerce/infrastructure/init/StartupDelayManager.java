package com.aug.ecommerce.infrastructure.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupDelayManager implements SmartLifecycle {

    private final AtomicBoolean ready = new AtomicBoolean(false);
    private boolean running = false;
    private final Environment env;

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String kafkaBootstrapServers;

    @Value("${spring.rabbitmq.host:localhost}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.port:5672}")
    private int rabbitPort;

    @Override
    public void start() {
        new Thread(() -> {
            try {
                log.info("[StartupDelayManager] Esperando 2 minutos...");
                Thread.sleep(2 * 60 * 1000); // 2 minutos
                onApplicationEvent();
                ready.set(true);
                log.info("[StartupDelayManager] Listo para ejecutar ApplicationRunner.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        running = true;
    }

    private void onApplicationEvent() {
        String profile = String.join(",", env.getActiveProfiles()).toLowerCase();
        log.info("[StartupDelayManager] Esperando readiness de infraestructura para perfil: " + profile);

        try {
            if (profile.contains("kafka")) {
                esperarKafkaListo();
            } else if (profile.contains("rabbit")) {
                esperarRabbitListo();
            }
        } catch (Exception e) {
            log.error("Error al esperar infraestructura: " + e.getMessage());
            return;
        }

        log.info("[StartupDelayManager] Infraestructura lista. Ejecutando carga inicial...");
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
                CachingConnectionFactory factory = new CachingConnectionFactory(rabbitHost, rabbitPort);
                factory.createConnection().close();
                log.info("RabbitMQ está listo");
                return;
            } catch (Exception e) {
                Thread.sleep(1000);
                intentos++;
            }
        }
        throw new IllegalStateException("RabbitMQ no respondió en el tiempo esperado");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public boolean isReady() {
        return ready.get();
    }

    // Para asegurarte que se ejecute al final
    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        running = false;
        callback.run();
    }

    @Override
    public void stop() {
        running = false;
    }
}
