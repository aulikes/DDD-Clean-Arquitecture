package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.orden.Orden;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdenRepository {
    void save(Orden orden);
    Optional<Orden> findById(UUID id);
    List<Orden> findAll();
    void deleteById(UUID id);

    List<Orden> findByClienteId(UUID clienteId);
    List<Orden> findByEstado(String estado);
}
