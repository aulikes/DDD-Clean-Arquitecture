package com.aug.ecommerce.domain.model.categoria;

import com.aug.ecommerce.domain.util.ValidadorDominio;

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
        this.nombre = ValidadorDominio.validarCampoObligatorio(nuevoNombre);
    }

    public void setDescripcion(String nuevaDescripcion) {
        this.descripcion = ValidadorDominio.validarCampoObligatorio(nuevaDescripcion);
    }

    public void inactivar() { this.activa = false; }
    public void activar() { this.activa = true; }
}
