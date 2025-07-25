package com.aug.ecommerce.domain.repositories;

import com.aug.ecommerce.domain.models.inventario.Inventario;
import java.util.List;
import java.util.Optional;

public interface InventarioRepository {
    void save(Inventario inventario);
    Optional<Inventario> findById(Long id);
    List<Inventario> findAll();

}
