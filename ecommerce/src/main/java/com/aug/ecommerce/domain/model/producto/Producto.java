package com.aug.ecommerce.domain.model.producto;

import com.aug.ecommerce.domain.util.ValidadorDominio;
import java.util.*;

public class Producto {
    private final Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagenUrl;

    private Long categoriaId;

    public Producto(Long id, String nombre, String descripcion, Double precio, String imagenUrl, Long categoriaId) {
        this.id = id;
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.setPrecio(precio);
        this.setImagenUrl(imagenUrl);
        this.setCategoriaId(categoriaId);
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Double getPrecio() { return precio; }
    public String getImagenUrl() { return imagenUrl; }
    public Long getCategoriaId() { return categoriaId; }

    public void setNombre(String nuevoNombre) {
        this.nombre = Objects.requireNonNull(nuevoNombre, "El nombre no puede ser null");
    }

    public void setDescripcion(String nuevaDescripcion) {
        this.descripcion = Objects.requireNonNull(nuevaDescripcion, "La descripción no puede ser null");
    }

    public void setPrecio(Double nuevoPrecio) {
        if (nuevoPrecio == null || nuevoPrecio < 0)
            throw new IllegalArgumentException("El precio no puede ser nulo ni negativo");
        this.precio = nuevoPrecio;
    }

    public void setImagenUrl(String nuevaImagenUrl) {
        this.imagenUrl =Objects.requireNonNull(nuevaImagenUrl, "La imagenUrl no puede ser null");
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}
