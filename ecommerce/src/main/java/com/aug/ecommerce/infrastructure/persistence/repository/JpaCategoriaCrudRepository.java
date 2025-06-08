package com.aug.ecommerce.infrastructure.persistence.repository;

import com.aug.ecommerce.domain.model.categoria.Categoria;
import com.aug.ecommerce.infrastructure.persistence.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaCategoriaCrudRepository extends JpaRepository<CategoriaEntity, Long> {

    @Query("select c from CategoriaEntity c where lower(c.nombre) = lower(:name)")
    List<Categoria> findByNombreIgnoreCase (@Param("name") String nombre);
}
