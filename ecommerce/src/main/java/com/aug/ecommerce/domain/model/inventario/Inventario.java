package com.aug.ecommerce.domain.model.inventario;

import java.util.Objects;

public class Inventario {
    private final Long productoId;
    private int stockDisponible;

    public Inventario(Long productoId, int stockInicial) {
        this.productoId = Objects.requireNonNull(productoId, "El productoId no puede ser nulo");
        if (stockInicial < 0)
            throw new IllegalArgumentException("El stock inicial no puede ser negativo");

        this.stockDisponible = stockInicial;
    }

    public Long getProductoId() { return productoId; }
    public int getStockDisponible() { return stockDisponible; }

    public void aumentarStock(int cantidad) {
        if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        this.stockDisponible += cantidad;
    }

    public void disminuirStock(int cantidad) {
        if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");

        if (cantidad > stockDisponible)
            throw new IllegalStateException("No hay suficiente stock disponible");

        this.stockDisponible -= cantidad;
    }

    public boolean tieneStockDisponible(int cantidadSolicitada) {
        return cantidadSolicitada > 0 && stockDisponible >= cantidadSolicitada;
    }
}
