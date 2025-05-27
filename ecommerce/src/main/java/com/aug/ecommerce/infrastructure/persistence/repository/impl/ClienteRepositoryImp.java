package com.aug.ecommerce.infrastructure.persistence.repository.impl;

import com.aug.ecommerce.domain.model.cliente.Cliente;
import com.aug.ecommerce.domain.repository.ClienteRepository;
import com.aug.ecommerce.infrastructure.persistence.mapper.ClienteMapper;
import com.aug.ecommerce.infrastructure.persistence.repository.contract.JpaClienteCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ClienteRepositoryImp implements ClienteRepository {

    private final JpaClienteCrudRepository jpa;

    @Override
    public void save(Cliente cliente) {
        jpa.save(ClienteMapper.toEntity(cliente));
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return jpa.findById(id).map(ClienteMapper::toDomain);
    }

    @Override
    public List<Cliente> findAll() {
        return jpa.findAll().stream().map(ClienteMapper::toDomain).toList();
    }

    @Override
    public Optional<Cliente> findByEmail(String email) {
        return jpa.findByEmail(email).map(ClienteMapper::toDomain);
    }
}
