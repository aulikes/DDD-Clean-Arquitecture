package com.aug.ecommerce.domain.repositories;

import com.aug.ecommerce.domain.models.orden.Orden;
import java.util.List;
import java.util.Optional;

public interface OrdenRepository {
    Orden save(Orden orden);
    Optional<Orden> findById(Long id);
    List<Orden> findAll();
    void deleteById(Long id);

    List<Orden> findByClienteId(Long clienteId);
    List<Orden> findByEstado(String estado);
}
