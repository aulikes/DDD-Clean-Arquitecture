package com.aug.ecommerce.infrastructure.persistence.repository.impl;

import com.aug.ecommerce.domain.model.cliente.Cliente;
import com.aug.ecommerce.domain.repository.ClienteRepository;
import com.aug.ecommerce.infrastructure.persistence.mapper.ClienteMapper;
import com.aug.ecommerce.infrastructure.persistence.repository.contract.JpaClienteCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class ClienteRepositoryImp implements ClienteRepository {

    private final JpaClienteCrudRepository jpa;
    private final ClienteMapper mapper;

    @Override
    public void save(Cliente cliente) {
        jpa.save(mapper.toEntity(cliente));
    }

    @Override
    public Optional<Cliente> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Cliente> findAll() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Cliente> findByEmail(String email) {
        return jpa.findByEmail(email).map(mapper::toDomain);
    }
}
