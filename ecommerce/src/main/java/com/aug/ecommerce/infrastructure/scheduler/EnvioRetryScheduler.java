
package com.aug.ecommerce.infrastructure.scheduler;

import com.aug.ecommerce.application.services.EnvioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnvioRetryScheduler {

    private final EnvioService service;

    /**
     * Ejecuta cada 3 minutos y reintenta enviar aquellos envíos pendientes.
     */
    @Scheduled(cron = "0 */3 * * * *")
    @Transactional
    public void reintentarEnviosPendientes() {
        Instant inicio = Instant.now();
        log.info(">>>>>>>>>>> Inicio del proceso reintentarEnviosPendientes: {}", inicio);

        service.reintentarEnvios();

        Instant fin = Instant.now();
        log.info(">>>>>>>>>>> Fin del proceso reintentarEnviosPendientes: {}", fin);
        log.info(">>>>>>>>>>> Duración total: {} ms", Duration.between(inicio, fin).toMillis());
    }
}
