package com.aug.ecommerce.domain.model.categoria;

import com.aug.ecommerce.domain.util.ValidadorDominio;
import java.util.*;

public class Categoria {
    private final Long id;
    private String nombre;
    private String descripcion;
    private boolean activa;
    private final Set<Long> productosIds;

    public Categoria(Long id, String nombre, String descripcion) {
        this.id = id;
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.activa = true;
        this.productosIds = new HashSet<>();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public boolean isActiva() { return activa; }
    public Set<Long> getProductosIds() {
        return Collections.unmodifiableSet(productosIds);
    }

    public void setNombre(String nuevoNombre) {
        this.nombre = ValidadorDominio.validarCampoObligatorio(nuevoNombre);
    }

    public void setDescripcion(String nuevaDescripcion) {
        this.descripcion = ValidadorDominio.validarCampoObligatorio(nuevaDescripcion);
    }

    public void inactivar() { this.activa = false; }
    public void activar() { this.activa = true; }

    public void agregarProducto(Long productoId) {
        productosIds.add(Objects.requireNonNull(productoId));
    }

    public void removerProducto(Long productoId) {
        productosIds.remove(Objects.requireNonNull(productoId));
    }
}
