package com.aug.ecommerce.infrastructure.persistence.adapter;

import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.repository.OrdenRepository;
import com.aug.ecommerce.infrastructure.persistence.mapper.OrdenMapper;
import com.aug.ecommerce.infrastructure.persistence.repository.JpaOrdenCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class OrdenRepositoryAdapter implements OrdenRepository {

    private final JpaOrdenCrudRepository jpa;

    @Override
    public Orden save(Orden orden) {
        return OrdenMapper.toDomain(jpa.save(OrdenMapper.toEntity(orden)));
    }

    @Override
    public Optional<Orden> findById(Long id) {
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
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Orden> findByClienteId(Long clienteId) {
        return jpa.findByClienteId(clienteId).stream().map(OrdenMapper::toDomain).toList();
    }
}

