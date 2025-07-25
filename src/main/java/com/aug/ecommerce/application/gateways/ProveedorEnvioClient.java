package com.aug.ecommerce.application.gateways;

import com.aug.ecommerce.application.dtos.ResultadoEnvioDTO;
import com.aug.ecommerce.domain.models.envio.Envio;

/**
 * Cliente que representa la integración con el proveedor externo de envíos.
 */
public interface ProveedorEnvioClient {
    ResultadoEnvioDTO prepararEnvio(Envio envio);
}
