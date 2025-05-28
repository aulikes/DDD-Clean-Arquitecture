package com.aug.ecommerce.application.gateway;

import com.aug.ecommerce.application.dto.ResultadoPagoDTO;
import com.aug.ecommerce.domain.model.pago.Pago;

public interface PasarelaPagoClient {
    ResultadoPagoDTO realizarPago(Pago pago);
}
