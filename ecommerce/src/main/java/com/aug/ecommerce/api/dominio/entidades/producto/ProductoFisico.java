package com.aug.ecommerce.api.dominio.entidades.producto;

import java.util.UUID;

public class ProductoFisico extends Producto {
    private double peso;

    public ProductoFisico(UUID id, String nombre, String descripcion, Double precio, String imagenUrl, double peso) {
        super(id, nombre, descripcion, precio, imagenUrl);
        if (peso < 0) throw new IllegalArgumentException("El peso no puede ser negativo");
        this.peso = peso;
    }

    public double getPeso() { return peso; }

    public void setPeso(double peso) {
        if (peso < 0) throw new IllegalArgumentException("El peso no puede ser negativo");
        this.peso = peso;
    }
}
