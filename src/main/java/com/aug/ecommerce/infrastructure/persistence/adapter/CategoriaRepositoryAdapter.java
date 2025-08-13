package com.aug.ecommerce.infrastructure.persistence.adapter;

import com.aug.ecommerce.domain.models.categoria.Categoria;
import com.aug.ecommerce.domain.repositories.CategoriaRepository;
import com.aug.ecommerce.infrastructure.persistence.entity.CategoriaEntity;
import com.aug.ecommerce.infrastructure.persistence.mapper.CategoriaMapper;
import com.aug.ecommerce.infrastructure.persistence.repository.JpaCategoriaCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CategoriaRepositoryAdapter implements CategoriaRepository {

    private final JpaCategoriaCrudRepository jpa;

    @Override
    public Categoria save(Categoria categoria) {
        return CategoriaMapper.toDomain(jpa.save(CategoriaMapper.toEntity(categoria)));
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        return jpa.findById(id).map(CategoriaMapper::toDomain);
    }

    @Override
    public List<Categoria> findAll() {
        return jpa.findAll().stream().map(CategoriaMapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Categoria> findByNombre(String nombre) {
        return jpa.findByNombreIgnoreCase(nombre);
    }
}
