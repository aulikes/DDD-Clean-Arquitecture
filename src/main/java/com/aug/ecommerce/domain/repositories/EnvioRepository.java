package com.aug.ecommerce.domain.repositories;

import com.aug.ecommerce.domain.models.envio.Envio;
import com.aug.ecommerce.domain.models.envio.EstadoEnvio;

import java.util.List;
import java.util.Optional;

public interface EnvioRepository {
    Envio saveWithHistorial(Envio envio);
    Optional<Envio> findById(Long id);
    Optional<Envio> findByIdWithHistorial(Long id);
    List<Envio> findByOrdenId(Long ordenId);
    List<Envio> findByEstado(EstadoEnvio estadoEnvio);
}
