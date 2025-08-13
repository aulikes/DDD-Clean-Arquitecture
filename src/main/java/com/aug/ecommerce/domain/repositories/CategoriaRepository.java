package com.aug.ecommerce.domain.repositories;

import com.aug.ecommerce.domain.models.categoria.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaRepository {
    Categoria save(Categoria categoria);
    Optional<Categoria> findById(Long id);
    List<Categoria> findAll();
    void deleteById(Long id);

    List<Categoria> findByNombre(String nombre);
}
