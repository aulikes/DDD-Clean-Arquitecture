package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.pago.Pago;
import java.util.List;
import java.util.Optional;

public interface PagoRepository {
    void save(Pago pago);
    Optional<Pago> findById(String id);
    List<Pago> findAll();
    void deleteById(String id);

    List<Pago> findByOrdenId(String ordenId);
    List<Pago> findByEstado(String estado);
}
