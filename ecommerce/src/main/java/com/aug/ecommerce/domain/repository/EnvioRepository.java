package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.envio.Envio;
import java.util.List;
import java.util.Optional;

public interface EnvioRepository {
    void save(Envio envio);
    Optional<Envio> findById(String id);
    List<Envio> findAll();
    void deleteById(String id);

    List<Envio> findByOrdenId(String ordenId);
}
