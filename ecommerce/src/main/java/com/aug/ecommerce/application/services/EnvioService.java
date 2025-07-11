package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.dtos.ResultadoEnvioDTO;
import com.aug.ecommerce.application.events.EnvioPreparadoEvent;
import com.aug.ecommerce.application.gateways.ProveedorEnvioClient;
import com.aug.ecommerce.application.publishers.EnvioEventPublisher;
import com.aug.ecommerce.domain.models.envio.Envio;
import com.aug.ecommerce.domain.repositories.EnvioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.function.BiConsumer;

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
        envio = envioRepository.saveWithHistorial(envio);
        return realizarEnvio(envio,
                (e, r) -> e.marcarErrorPendiente(r.mensaje()));
    }

    @Transactional
    public void reintentarEnvios(){
        List<Envio> pendientes = envioRepository.findByEstado(Envio.getEstadoInicial());
        for (Envio envio : pendientes) {
            this.realizarEnvio(envio,
                    (e, r) -> e.agregarEstadoPendiente(r.mensaje()));
        }
    }

    /**
     * Utilizamos Lambda Consumer para ejecutar la función adecuada con base al método que lo llama
     * - Si viene de crearEnvio, NO se debe agregar nuevo estado, ya se agregó es esa función
     * - Si viene de reintentarEnvios, se debe agregar nuevo estado
     */
    @Transactional
    private Envio realizarEnvio(Envio envio, BiConsumer<Envio, ResultadoEnvioDTO> onError){
        // Invocar sistema externo para ejecutar el envío
        ResultadoEnvioDTO resultadoEnvio = proveedorEnvioClient.prepararEnvio(envio);
        envio.incrementarReintentos(); // se adiciona el intento de realizar el envío
        // Si fue exitoso, actualizar estado a ENVIADO
        if (resultadoEnvio.exitoso()) {
            envio.iniciarPreparacionEnvio(resultadoEnvio.trackingNumber());
        } else{
            log.error("Error al reenviar envío ID {}: {}", envio.getId(), resultadoEnvio.mensaje());
            if (envio.getIntentos() >= MAX_REINTENTOS) {
                envio.agregarEstadoFallido("Excedido número máximo de reintentos.");
            }
            else{// Llama al lambda
                onError.accept(envio, resultadoEnvio);
            }
        }
        envio = envioRepository.saveWithHistorial(envio);
        publicarEventoEnvio(envio, resultadoEnvio);
        return envio;
    }

    private void publicarEventoEnvio(Envio envio, ResultadoEnvioDTO resultadoEnvio){
        envioEventPublisher.publicarEnvioPreparado(
        new EnvioPreparadoEvent(envio.getOrdenId(), envio.getId(), Instant.now(),
                resultadoEnvio.exitoso(), resultadoEnvio.trackingNumber(),
                resultadoEnvio.mensaje()));
    }
}
