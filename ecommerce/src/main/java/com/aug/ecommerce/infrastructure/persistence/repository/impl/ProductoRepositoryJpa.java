package com.aug.ecommerce.infrastructure.persistence.repository.impl;

import com.aug.ecommerce.domain.model.producto.Producto;
import com.aug.ecommerce.domain.repository.ProductoRepository;
import com.aug.ecommerce.infrastructure.persistence.mapper.ProductoMapper;
import com.aug.ecommerce.infrastructure.persistence.repository.contract.JpaProductoCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductoRepositoryJpa implements ProductoRepository {

    private final JpaProductoCrudRepository jpa;

    public ProductoRepositoryJpa(JpaProductoCrudRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Producto producto) {
        jpa.save(ProductoMapper.toEntity(producto));
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return jpa.findById(id).map(ProductoMapper::toDomain);
    }

    @Override
    public List<Producto> findAll() {
        return jpa.findAll().stream().map(ProductoMapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
