package com.aug.ecommerce.infrastructure.external.delivery;

import com.aug.ecommerce.application.dto.ResultadoEnvioDTO;
import com.aug.ecommerce.application.gateway.ProveedorEnvioClient;
import com.aug.ecommerce.domain.model.envio.Envio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProveedorEnvioHttpClient implements ProveedorEnvioClient {

    @Override
    public ResultadoEnvioDTO prepararEnvio(Envio envio) {
        Random random = new Random();
        int probabilidad = random.nextInt(100);
        ResultadoEnvioDTO result;
        if (probabilidad >= 80) {
            result = new ResultadoEnvioDTO(
                    false,
                    null,
                    null,
                    "Simulación de timeout en FEDEX"
            );
        }
        else{
            try {
                // Simula latencia entre 1 y 3 segundos
                Thread.sleep(random.nextInt(2000) + 1000);

                if (probabilidad > 50) {
                    result = new ResultadoEnvioDTO(
                            false,
                            null,
                            null,
                            "Error de Servidor"
                    );
                } else {
                    result = new ResultadoEnvioDTO(
                            true,
                            "TRX-FEDEX-" + UUID.randomUUID(),
                            "PREPARANDO",
                            "Envío generado exitosamente."
                    );
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Error de latencia simulada", e);
                result = new ResultadoEnvioDTO(
                        false,
                        null,
                        null,
                        "Error de latencia simulada"
                );
            }
        }
        return result;
    }
}
