package com.aug.ecommerce.domain.model.cliente;

import java.util.*;

public class Cliente {
    private final Long id;
    private String nombre;
    private String email;
    private final List<Direccion> direcciones;

    public Cliente(Long id, String nombre, String email) {
        this.id = id;
        this.setNombre(nombre);
        this.setEmail(email);
        this.direcciones = new ArrayList<>();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public List<Direccion> getDirecciones() {
        return Collections.unmodifiableList(direcciones);
    }

    public void setNombre(String nuevoNombre) {
        this.nombre = Objects.requireNonNull(nuevoNombre, "El nombre no puede ser nulo");
    }

    public void setEmail(String nuevoEmail) {
        this.email = Objects.requireNonNull(nuevoEmail, "El email no puede ser nulo");;
    }

    public Direccion agregarDireccion(String calle, String ciudad, String pais, String codigoPostal) {
        Direccion direccion = new Direccion(calle, ciudad, pais, codigoPostal);
        direcciones.add(direccion);
        return direccion;
    }

    public Direccion actualizarDireccion(
            UUID direccionId, String nuevaCalle, String nuevaCiudad, String nuevoPais, String nuevoCodigoPostal) {

        Direccion direccion = buscarDireccionPorId(direccionId)
                .orElseThrow(() -> new NoSuchElementException("Dirección no encontrada"));
        direccion.actualizar(nuevaCalle, nuevaCiudad, nuevoPais, nuevoCodigoPostal);
        return direccion;
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
