package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.orden.Orden;
import java.util.List;
import java.util.Optional;

public interface OrdenRepository {
    void save(Orden orden);
    Optional<Orden> findById(String id);
    List<Orden> findAll();
    void deleteById(String id);

    List<Orden> findByClienteId(String clienteId);
    List<Orden> findByEstado(String estado);
}
