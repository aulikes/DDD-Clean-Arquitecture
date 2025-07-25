package com.aug.ecommerce.application.gateways;

import com.aug.ecommerce.application.dtos.ResultadoPagoDTO;
import com.aug.ecommerce.domain.models.pago.Pago;

import java.util.concurrent.TimeoutException;

public interface PasarelaPagoClient {
    ResultadoPagoDTO realizarPago(Pago pago) throws TimeoutException;
}
