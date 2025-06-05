package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.envio.Envio;
import com.aug.ecommerce.domain.model.envio.EstadoEnvio;

import java.util.List;
import java.util.Optional;

public interface EnvioRepository {
    Envio save(Envio envio);
    Optional<Envio> findById(Long id);
    List<Envio> findByOrdenId(Long ordenId);
    List<Envio> findByEstado(EstadoEnvio estadoEnvio, int maxIntentos);
}
