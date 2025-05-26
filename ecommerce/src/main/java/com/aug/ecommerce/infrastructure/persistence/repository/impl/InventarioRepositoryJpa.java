package com.aug.ecommerce.infrastructure.persistence.repository.impl;

import com.aug.ecommerce.domain.model.inventario.Inventario;
import com.aug.ecommerce.domain.repository.InventarioRepository;
import com.aug.ecommerce.infrastructure.persistence.mapper.InventarioMapper;
import com.aug.ecommerce.infrastructure.persistence.repository.contract.JpaInventarioCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InventarioRepositoryJpa implements InventarioRepository {

    private final JpaInventarioCrudRepository jpa;

    public InventarioRepositoryJpa(JpaInventarioCrudRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Inventario inventario) {
        jpa.save(InventarioMapper.toEntity(inventario));
    }

    @Override
    public Optional<Inventario> findById(UUID id) {
        return jpa.findById(id).map(InventarioMapper::toDomain);
    }

    @Override
    public Optional<Inventario> findByProductoId(UUID productoId) {
        return jpa.findByProductoId(productoId).map(InventarioMapper::toDomain);
    }

    @Override
    public List<Inventario> findAll() {
        return jpa.findAll().stream().map(InventarioMapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}
