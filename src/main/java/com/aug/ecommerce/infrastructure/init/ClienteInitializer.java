package com.aug.ecommerce.infrastructure.init;

import com.aug.ecommerce.adapters.rest.dtos.CrearClienteRequestDTO;
import com.aug.ecommerce.adapters.rest.mappers.ClienteMapper;
import com.aug.ecommerce.application.services.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteInitializer {

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    @Transactional
    public void run() {
        CrearClienteRequestDTO juan = new CrearClienteRequestDTO("Juan Pérez", "juan@ecommerce.com", new ArrayList<>());
        juan.getDirecciones().add(new CrearClienteRequestDTO.Direccion("Calle 123", "Bogotá", "Colombia", "110111"));

        CrearClienteRequestDTO laura = new CrearClienteRequestDTO("Laura Gómez", "laura@ecommerce.com", new ArrayList<>());
        laura.getDirecciones().add(new CrearClienteRequestDTO.Direccion("Av. Siempre Viva 742", "Medellín", "Colombia", "050001"));

        CrearClienteRequestDTO carlos = new CrearClienteRequestDTO("Carlos Ruiz", "carlos@ecommerce.com", new ArrayList<>());
        carlos.getDirecciones().add(new CrearClienteRequestDTO.Direccion("Carrera 7", "Cali", "Colombia", "760001"));

        List<CrearClienteRequestDTO> clientes = List.of(juan, laura, carlos);
        clientes.forEach(c -> clienteService.crearCliente(clienteMapper.toCommand(c)));

        log.info(">>> Clientes iniciales creados");
    }
}
