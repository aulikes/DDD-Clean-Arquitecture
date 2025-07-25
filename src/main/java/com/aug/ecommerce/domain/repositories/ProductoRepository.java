package com.aug.ecommerce.domain.repositories;

import com.aug.ecommerce.domain.models.producto.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    Producto save(Producto producto);
    Optional<Producto> findById(Long id);
    List<Producto> findAll();
    void deleteById(Long id);
}
