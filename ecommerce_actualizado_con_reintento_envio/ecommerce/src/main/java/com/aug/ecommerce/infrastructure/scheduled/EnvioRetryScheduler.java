
package com.aug.ecommerce.infrastructure.scheduled;

import com.aug.ecommerce.domain.model.envio.Envio;
import com.aug.ecommerce.domain.repository.EnvioRepository;
import com.aug.ecommerce.infrastructure.external.EnvioExternoClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnvioRetryScheduler {

    private final EnvioRepository envioRepository;
    private final EnvioExternoClient envioExternoClient;

    private static final int MAX_REINTENTOS = 3;

    /**
     * Ejecuta cada 5 minutos y reintenta enviar aquellos envíos pendientes.
     */
    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void reintentarEnviosPendientes() {
        List<Envio> pendientes = envioRepository.findByEstadoPendiente();

        for (Envio envio : pendientes) {
            try {
                log.info("Reintentando envío con ID: {}", envio.getId());

                envioExternoClient.enviar(envio); // intento real

                envio.marcarComoPreparado(); // suponiendo que este método existe
                envioRepository.save(envio);

                log.info("Envío marcado como PREPARADO para ID: {}", envio.getId());

            } catch (Exception ex) {
                log.error("Error al reenviar envío ID {}: {}", envio.getId(), ex.getMessage());

                envio.incrementarReintentos(); // se debe persistir este conteo
                if (envio.getIntentos() >= MAX_REINTENTOS) {
                    envio.marcarComoFallido("Excedido número máximo de reintentos.");
                }

                envioRepository.save(envio);
            }
        }
    }
}
