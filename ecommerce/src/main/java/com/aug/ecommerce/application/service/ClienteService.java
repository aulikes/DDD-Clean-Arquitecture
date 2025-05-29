package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.command.CrearClienteCommand;
import com.aug.ecommerce.domain.model.cliente.Cliente;
import com.aug.ecommerce.domain.model.cliente.Direccion;
import com.aug.ecommerce.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente crearCliente(CrearClienteCommand cliente) {
        Cliente newClient = new Cliente(null, cliente.getNombre(), cliente.getEmail());
        for (CrearClienteCommand.Direccion dir : cliente.getDirecciones()) {
            newClient.agregarDireccion(dir.getCalle(), dir.getCiudad(), dir.getPais(), dir.getCodigoPostal());
        }
        clienteRepository.save(newClient);
        return newClient;
    }

    @Transactional
    public Cliente actualizarNombre(CrearClienteCommand cliente) {
        Cliente clienteDom = clienteRepository.findById(cliente.getId()).orElseThrow(() ->
                new IllegalArgumentException("Cliente no encontrado")
        );
        clienteDom.setNombre(cliente.getNombre());
        clienteRepository.save(clienteDom);
        return clienteDom;
    }

    @Transactional
    public Cliente actualizarEmail(CrearClienteCommand cliente) {
        Cliente clienteDom = clienteRepository.findById(cliente.getId()).orElseThrow(() ->
                new IllegalArgumentException("Cliente no encontrado")
        );
        clienteDom.setEmail(cliente.getEmail());
        clienteRepository.save(clienteDom);
        return clienteDom;
    }

    @Transactional
    public Direccion agregarDireccion(
            Long clienteId, String calle, String ciudad, String pais, String codigoPostal) {
        Cliente clienteDom = clienteRepository.findById(clienteId).orElseThrow(() ->
                new IllegalArgumentException("Cliente no encontrado")
        );
        return clienteDom.agregarDireccion(calle, ciudad, pais, codigoPostal);
    }

    @Transactional
    public void actualizarDireccion(
            Long clienteId, UUID direccionId, String nuevaCalle, String nuevaCiudad,  String nuevoPais,
            String nuevoCodigoPostal
    ) {
        Cliente clienteDom = clienteRepository.findById(clienteId).orElseThrow(() ->
                new IllegalArgumentException("Cliente no encontrado")
        );
        clienteDom.actualizarDireccion(direccionId, nuevaCalle, nuevaCiudad, nuevoPais, nuevoCodigoPostal);
    }

    @Transactional
    public void eliminarDireccion(Cliente cliente, UUID direccionId) {
        cliente.eliminarDireccion(direccionId);
    }

    @Transactional
    public List<Cliente> getAll(){
        return clienteRepository.findAll();
    }
}
