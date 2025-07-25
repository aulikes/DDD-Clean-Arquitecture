package com.aug.ecommerce.domain.repositories;

import com.aug.ecommerce.domain.models.cliente.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    void save(Cliente cliente);
    Optional<Cliente> findById(Long id);
    List<Cliente> findAll();

    Optional<Cliente> findByEmail(String email);
}
