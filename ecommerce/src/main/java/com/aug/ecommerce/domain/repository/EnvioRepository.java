package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.envio.Envio;
import java.util.List;
import java.util.Optional;

public interface EnvioRepository {
    void save(Envio envio);
    Optional<Envio> findById(Long id);
    List<Envio> findAll();
    void deleteById(Long id);

    List<Envio> findByOrdenId(Long ordenId);
}
