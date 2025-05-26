package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.cliente.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    void save(Cliente cliente);
    Optional<Cliente> findById(String id);
    List<Cliente> findAll();
    void deleteById(String id);

    Optional<Cliente> findByEmail(String email);
}
