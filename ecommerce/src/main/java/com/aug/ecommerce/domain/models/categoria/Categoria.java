package com.aug.ecommerce.domain.models.categoria;

import java.util.Objects;

public class Categoria {
    private final Long id;
    private String nombre;
    private String descripcion;
    private boolean activa;

    public Categoria(Long id, String nombre, String descripcion) {
        this.id = id;
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.activa = true;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public boolean isActiva() { return activa; }

    public void setNombre(String nuevoNombre) {
        this.nombre = Objects.requireNonNull(nuevoNombre, "El nombre no puede ser nulo");
    }

    public void setDescripcion(String nuevaDescripcion) {
        this.descripcion = Objects.requireNonNull(nuevaDescripcion, "La descripcion no puede ser nula");
    }

    public void inactivar() { this.activa = false; }
    public void activar() { this.activa = true; }
}
