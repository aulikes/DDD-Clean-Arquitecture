package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.dto.ResultadoEnvioDTO;
import com.aug.ecommerce.application.gateway.ProveedorEnvioClient;
import com.aug.ecommerce.domain.model.envio.Envio;
import com.aug.ecommerce.domain.model.envio.EstadoEnvio;
import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.repository.EnvioRepository;
import com.aug.ecommerce.domain.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final ProveedorEnvioClient proveedorEnvioClient;

    @Transactional
    public Envio prepararEnvio(Long ordenId, String direccionEntrega) {
        // Crear envío con estado PENDIENTE
        Envio envio = Envio.create(ordenId, direccionEntrega);
        // Guardar Envío en BD
        envio = envioRepository.save(envio);
        // Invocar sistema externo para ejecutar el envío
        ResultadoEnvioDTO resultadoEnvio = proveedorEnvioClient.realizarEnvio(envio);
        // Si fue exitoso, actualizar estado a ENVIADO
        if (resultadoEnvio.exitoso()) {
            envio.prepararEnvio(resultadoEnvio.trackingNumber());
            envio = envioRepository.save(envio);
        }
        return envio;
    }

    @Transactional
    public void actualizarEstadoEnvio(Long envioId, EstadoEnvio nuevoEstado) {
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new IllegalArgumentException("Envío no encontrado"));
        envio.actualizarEstadoFromSupplier(nuevoEstado);
        envioRepository.save(envio);
    }
}
