package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.inventario.Inventario;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventarioRepository {
    void save(Inventario inventario);
    Optional<Inventario> findById(String id);
    List<Inventario> findAll();
    void deleteById(String id);

    Optional<Inventario> findByProductoId(UUID productoId);
}
