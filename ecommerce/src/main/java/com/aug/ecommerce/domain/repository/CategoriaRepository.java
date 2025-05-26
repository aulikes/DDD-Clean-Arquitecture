package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.categoria.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaRepository {
    void save(Categoria categoria);
    Optional<Categoria> findById(String id);
    List<Categoria> findAll();
    void deleteById(String id);

    Optional<Categoria> findByNombre(String nombre);
}
