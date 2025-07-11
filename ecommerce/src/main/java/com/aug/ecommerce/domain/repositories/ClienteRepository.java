package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.cliente.Cliente;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository {
    void save(Cliente cliente);
    Optional<Cliente> findById(Long id);
    List<Cliente> findAll();

    Optional<Cliente> findByEmail(String email);
}
