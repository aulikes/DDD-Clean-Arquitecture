package com.aug.ecommerce.application;

import com.aug.ecommerce.domain.model.cliente.Cliente;
import com.aug.ecommerce.domain.model.cliente.Direccion;

import java.util.UUID;

public class ServicioAplicacionCliente {

    public Cliente crearCliente(UUID id, String nombre, String email) {
        return new Cliente(id, nombre, email);
    }

    public void actualizarNombre(Cliente cliente, String nuevoNombre) {
        cliente.setNombre(nuevoNombre);
    }

    public void actualizarEmail(Cliente cliente, String nuevoEmail) {
        cliente.setEmail(nuevoEmail);
    }

    public Direccion agregarDireccion(Cliente cliente, String calle, String ciudad, String pais, String codigoPostal) {
        return cliente.agregarDireccion(calle, ciudad, pais, codigoPostal);
    }

    public void actualizarDireccion(
            Cliente cliente,
            UUID direccionId,
            String nuevaCalle,
            String nuevaCiudad,
            String nuevoPais,
            String nuevoCodigoPostal
    ) {
        cliente.actualizarDireccion(direccionId, nuevaCalle, nuevaCiudad, nuevoPais, nuevoCodigoPostal);
    }

    public void eliminarDireccion(Cliente cliente, UUID direccionId) {
        cliente.eliminarDireccion(direccionId);
    }
}
