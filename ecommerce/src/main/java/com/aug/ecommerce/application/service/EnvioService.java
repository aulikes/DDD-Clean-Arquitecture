package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.dto.ResultadoEnvioDTO;
import com.aug.ecommerce.application.event.EnvioPreparadoEvent;
import com.aug.ecommerce.application.event.PagoConfirmadoEvent;
import com.aug.ecommerce.application.gateway.ProveedorEnvioClient;
import com.aug.ecommerce.application.publisher.EnvioEventPublisher;
import com.aug.ecommerce.domain.model.envio.Envio;
import com.aug.ecommerce.domain.model.envio.EstadoEnvio;
import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.repository.EnvioRepository;
import com.aug.ecommerce.domain.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnvioService {
    private static final int MAX_REINTENTOS = 3;

    private final EnvioRepository envioRepository;
    private final ProveedorEnvioClient proveedorEnvioClient;
    private final EnvioEventPublisher envioEventPublisher;

    @Transactional
    public Envio crearEnvio(Long ordenId, String direccionEntrega) {
        // Crear envío con estado PENDIENTE
        Envio envio = Envio.create(ordenId, direccionEntrega);
        // Guardar Envío en BD
        envio = envioRepository.save(envio);
        return prepararEnvio(envio);
    }

    public void reintentarEnvios(){
        List<Envio> pendientes = envioRepository.findByEstado(Envio.getEstadoInicial(), MAX_REINTENTOS);
        for (Envio envio : pendientes) {
            this.prepararEnvio(envio);
        }
    }

    @Transactional
    private Envio prepararEnvio(Envio envio){
        // Invocar sistema externo para ejecutar el envío
        ResultadoEnvioDTO resultadoEnvio = proveedorEnvioClient.prepararEnvio(envio);
        // Si fue exitoso, actualizar estado a ENVIADO
        if (resultadoEnvio.exitoso()) {
            envio.prepararEnvio(resultadoEnvio.trackingNumber());
            envio = envioRepository.save(envio);
            envioEventPublisher.publicarEnvioPreparado(
            new EnvioPreparadoEvent(envio.getOrdenId(), envio.getId(), Instant.now(),
                    resultadoEnvio.exitoso(), resultadoEnvio.trackingNumber(),
                    resultadoEnvio.mensaje()));
        } else{
            log.error("Error al reenviar envío ID {}: {}", envio.getId(), resultadoEnvio.mensaje());
            envio.incrementarReintentos(); // se debe persistir este conteo
            if (envio.getIntentos() > MAX_REINTENTOS) {
                envio.marcarComoFallido("Excedido número máximo de reintentos.");
            }
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
