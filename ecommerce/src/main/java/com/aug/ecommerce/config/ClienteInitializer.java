package com.aug.ecommerce.config;

import com.aug.ecommerce.domain.model.cliente.Cliente;
import com.aug.ecommerce.domain.repository.ClienteRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClienteInitializer {

    private final ClienteRepository clienteRepository;

    @PostConstruct
    public void init() {
        if (clienteRepository.findAll().isEmpty()) {
            Cliente juan = new Cliente(UUID.randomUUID(), "Juan Pérez", "juan@ecommerce.com");
            juan.agregarDireccion("Calle 123", "Bogotá", "Colombia", "110111");

            Cliente laura = new Cliente(UUID.randomUUID(), "Laura Gómez", "laura@ecommerce.com");
            laura.agregarDireccion("Av. Siempre Viva 742", "Medellín", "Colombia", "050001");

            Cliente carlos = new Cliente(UUID.randomUUID(), "Carlos Ruiz", "carlos@ecommerce.com");
            carlos.agregarDireccion("Carrera 7", "Cali", "Colombia", "760001");

            List<Cliente> clientes = List.of(juan, laura, carlos);
            clientes.forEach(clienteRepository::save);
        }
    }
}
