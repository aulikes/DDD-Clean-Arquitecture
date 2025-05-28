package com.aug.ecommerce.infrastructure.external.pago;

import com.aug.ecommerce.application.dto.ResultadoPagoDTO;
import com.aug.ecommerce.application.gateway.PasarelaPagoClient;
import com.aug.ecommerce.domain.model.pago.Pago;
import org.springframework.stereotype.Component;

@Component
public class WompiPasarelaPagoClient implements PasarelaPagoClient {

    @Override
    public ResultadoPagoDTO realizarPago(Pago pago) {
        // Aquí iría la llamada real con WebClient o Feign

        // Simulación de respuesta:
        return new ResultadoPagoDTO(
                true,
                "TRX-WOMPI-123456",
                "Pago procesado correctamente por Wompi"
        );
    }
}
