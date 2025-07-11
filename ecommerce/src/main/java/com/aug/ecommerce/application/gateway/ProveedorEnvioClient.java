package com.aug.ecommerce.application.gateway;

import com.aug.ecommerce.application.dtos.ResultadoEnvioDTO;
import com.aug.ecommerce.domain.model.envio.Envio;

/**
 * Cliente que representa la integración con el proveedor externo de envíos.
 */
public interface ProveedorEnvioClient {
    ResultadoEnvioDTO prepararEnvio(Envio envio);
}
