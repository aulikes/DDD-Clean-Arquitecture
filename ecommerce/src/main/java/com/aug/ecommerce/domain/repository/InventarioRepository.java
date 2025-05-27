package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.inventario.Inventario;
import java.util.List;
import java.util.Optional;

public interface InventarioRepository {
    void save(Inventario inventario);
    Optional<Inventario> findById(Long id);
    List<Inventario> findAll();
    void deleteById(Long id);

    Optional<Inventario> findByProductoId(Long productoId);
}
