
package com.aug.ecommerce.infrastructure.scheduled;

import com.aug.ecommerce.application.service.EnvioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnvioRetryScheduler {

    private final EnvioService service;

    /**
     * Ejecuta cada 3 minutos y reintenta enviar aquellos envíos pendientes.
     */
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void reintentarEnviosPendientes() {
        service.reintentarEnvios();
    }
}
