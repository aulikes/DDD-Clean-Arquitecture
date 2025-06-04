package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.pago.Pago;
import java.util.List;
import java.util.Optional;

public interface PagoRepository {
    Pago save(Pago pago);
    Optional<Pago> findById(Long id);
    List<Pago> findByOrdenId(Long ordenId);
}
