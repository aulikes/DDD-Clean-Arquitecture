package com.aug.ecommerce.config;

import com.aug.ecommerce.adapters.rest.dto.CrearClienteRequestDTO;
import com.aug.ecommerce.adapters.rest.mapper.ClienteMapper;
import com.aug.ecommerce.application.service.ClienteService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClienteInitializer {

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    @PostConstruct
    public void init() {
        CrearClienteRequestDTO juan = new CrearClienteRequestDTO("Juan Pérez", "juan@ecommerce.com", new ArrayList<>());
        juan.getDirecciones().add(new CrearClienteRequestDTO.Direccion("Calle 123", "Bogotá", "Colombia", "110111"));

        CrearClienteRequestDTO laura = new CrearClienteRequestDTO("Laura Gómez", "laura@ecommerce.com", new ArrayList<>());
        laura.getDirecciones().add(new CrearClienteRequestDTO.Direccion("Av. Siempre Viva 742", "Medellín", "Colombia", "050001"));

        CrearClienteRequestDTO carlos = new CrearClienteRequestDTO("Carlos Ruiz", "carlos@ecommerce.com", new ArrayList<>());
        carlos.getDirecciones().add(new CrearClienteRequestDTO.Direccion("Carrera 7", "Cali", "Colombia", "760001"));

        List<CrearClienteRequestDTO> clientes = List.of(juan, laura, carlos);
        clientes.forEach(c -> clienteService.crearCliente(clienteMapper.toCommand(c)));
    }
}
