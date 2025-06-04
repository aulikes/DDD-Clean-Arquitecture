package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.producto.Producto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoRepository {
    Producto save(Producto producto);
    Optional<Producto> findById(Long id);
    List<Producto> findAll();
    void deleteById(Long id);

//    List<Producto> findByCategoriaId(UUID categoriaId);
//    List<Producto> findByNombreContaining(String nombre);
}
