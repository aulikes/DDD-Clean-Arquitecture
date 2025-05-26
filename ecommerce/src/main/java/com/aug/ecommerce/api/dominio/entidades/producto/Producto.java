package com.aug.ecommerce.api.dominio.entidades.producto;

import com.aug.ecommerce.api.dominio.utils.ValidadorDominio;
import java.util.*;

public abstract class Producto {
    private final UUID id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagenUrl;
    private final Set<UUID> categoriasIds;

    public Producto(UUID id, String nombre, String descripcion, Double precio, String imagenUrl) {
        this.id = Objects.requireNonNull(id, "El id no puede ser nulo");
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.setPrecio(precio);
        this.setImagenUrl(imagenUrl);
        this.categoriasIds = new HashSet<>();
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Double getPrecio() { return precio; }
    public String getImagenUrl() { return imagenUrl; }
    public Set<UUID> getCategoriasIds() {
        return Collections.unmodifiableSet(categoriasIds);
    }

    public void setNombre(String nuevoNombre) {
        this.nombre = ValidadorDominio.validarCampoObligatorio(nuevoNombre);
    }

    public void setDescripcion(String nuevaDescripcion) {
        this.descripcion = ValidadorDominio.validarCampoObligatorio(nuevaDescripcion);
    }

    public void setPrecio(Double nuevoPrecio) {
        if (nuevoPrecio == null || nuevoPrecio < 0)
            throw new IllegalArgumentException("El precio no puede ser nulo ni negativo");
        this.precio = nuevoPrecio;
    }

    public void setImagenUrl(String nuevaImagenUrl) {
        this.imagenUrl = ValidadorDominio.validarCampoObligatorio(nuevaImagenUrl);
    }

    // Gestión de categorías
    public void agregarCategoria(UUID categoriaId) {
        categoriasIds.add(Objects.requireNonNull(categoriaId));
    }

    public void removerCategoria(UUID categoriaId) {
        categoriasIds.remove(Objects.requireNonNull(categoriaId));
    }
}
