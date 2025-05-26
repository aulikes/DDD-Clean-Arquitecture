package com.aug.ecommerce.api.dominio.entidades.cliente;

import com.aug.ecommerce.api.dominio.utils.ValidadorDominio;

import java.util.*;

public class Cliente {
    private final UUID id;
    private String nombre;
    private String email;
    private final List<Direccion> direcciones;

    public Cliente(UUID id, String nombre, String email) {
        this.id = Objects.requireNonNull(id, "El id no puede ser nulo");
        this.setNombre(nombre);
        this.setEmail(email);
        this.direcciones = new ArrayList<>();
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public List<Direccion> getDirecciones() {
        return Collections.unmodifiableList(direcciones);
    }

    public void setNombre(String nuevoNombre) {
        this.nombre = ValidadorDominio.validarCampoObligatorio(nuevoNombre);
    }

    public void setEmail(String nuevoEmail) {
        this.email = ValidadorDominio.validarCampoObligatorio(nuevoEmail);
    }

    public Direccion agregarDireccion(String calle, String ciudad, String pais, String codigoPostal) {
        Direccion direccion = new Direccion(UUID.randomUUID(), calle, ciudad, pais, codigoPostal);
        direcciones.add(direccion);
        return direccion;
    }

    public void actualizarDireccion(
            UUID direccionId, String nuevaCalle, String nuevaCiudad, String nuevoPais, String nuevoCodigoPostal) {

        Direccion direccion = buscarDireccionPorId(direccionId)
                .orElseThrow(() -> new NoSuchElementException("Dirección no encontrada"));
        direccion.actualizar(nuevaCalle, nuevaCiudad, nuevoPais, nuevoCodigoPostal);
    }

    public boolean eliminarDireccion(UUID direccionId) {
        return direcciones.removeIf(dir -> dir.getId().equals(direccionId));
    }

    public Optional<Direccion> buscarDireccionPorId(UUID direccionId) {
        return direcciones.stream()
                .filter(dir -> dir.getId().equals(direccionId))
                .findFirst();
    }
}
