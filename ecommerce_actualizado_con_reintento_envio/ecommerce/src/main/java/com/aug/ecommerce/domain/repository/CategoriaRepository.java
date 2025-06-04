package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.categoria.Categoria;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository {
    void save(Categoria categoria);
    Optional<Categoria> findById(Long id);
    List<Categoria> findAll();
    void deleteById(Long id);

    List<Categoria> findByNombre(String nombre);
}
