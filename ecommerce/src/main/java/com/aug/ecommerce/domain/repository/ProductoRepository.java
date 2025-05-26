package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.producto.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    void save(Producto producto);
    Optional<Producto> findById(String id);
    List<Producto> findAll();
    void deleteById(String id);

    List<Producto> findByCategoriaId(String categoriaId);
    List<Producto> findByNombreContaining(String nombre);
}
