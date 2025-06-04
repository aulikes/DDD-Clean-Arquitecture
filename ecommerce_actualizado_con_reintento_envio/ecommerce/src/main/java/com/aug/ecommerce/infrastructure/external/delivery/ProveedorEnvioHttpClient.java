package com.aug.ecommerce.infrastructure.external.delivery;

import com.aug.ecommerce.application.dto.ResultadoEnvioDTO;
import com.aug.ecommerce.application.gateway.ProveedorEnvioClient;
import com.aug.ecommerce.domain.model.envio.Envio;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProveedorEnvioHttpClient implements ProveedorEnvioClient {

    @Override
    public ResultadoEnvioDTO prepararEnvio(Envio envio) {
        // Simulación: siempre exitoso, genera tracking
        String trackingNumber = "TRACK-" + UUID.randomUUID();
        return new ResultadoEnvioDTO(true, "PREPARANDO", trackingNumber, "Envío generado exitosamente.");
    }
}
