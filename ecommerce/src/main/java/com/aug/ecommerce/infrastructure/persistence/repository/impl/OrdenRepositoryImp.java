package com.aug.ecommerce.infrastructure.persistence.repository.impl;

import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.repository.OrdenRepository;
import com.aug.ecommerce.infrastructure.persistence.mapper.OrdenMapper;
import com.aug.ecommerce.infrastructure.persistence.repository.contract.JpaOrdenCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class OrdenRepositoryImp implements OrdenRepository {

    private final JpaOrdenCrudRepository jpa;

    @Override
    public void save(Orden orden) {
        jpa.save(OrdenMapper.toEntity(orden));
    }

    @Override
    public Optional<Orden> findById(UUID id) {
        return jpa.findById(id).map(OrdenMapper::toDomain);
    }

    @Override
    public List<Orden> findAll() {
        return jpa.findAll().stream().map(OrdenMapper::toDomain).toList();
    }

    @Override
    public List<Orden> findByEstado(String estado) {
        return List.of();
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Orden> findByClienteId(UUID clienteId) {
        return jpa.findByClienteId(clienteId).stream().map(OrdenMapper::toDomain).toList();
    }
}

