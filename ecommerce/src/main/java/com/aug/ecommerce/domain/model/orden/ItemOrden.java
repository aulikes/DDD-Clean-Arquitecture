package com.aug.ecommerce.domain.model.orden;

import java.util.Objects;

public final class ItemOrden {
    private final Long id;
    private final Long productoId;
    private int cantidad;
    private double precioUnitario;

    ItemOrden(Long id, Long productoId, int cantidad, double precioUnitario) {
        this.id = Objects.requireNonNull(id, "El id no puede ser nulo");
        this.productoId = Objects.requireNonNull(productoId, "El productoId no puede ser nulo");
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        if (precioUnitario < 0) throw new IllegalArgumentException("El precio no puede ser negativo");
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Long getId() { return id; }
    public Long getProductoId() { return productoId; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return cantidad * precioUnitario; }

    void cambiarCantidad(int nuevaCantidad) {
        if (nuevaCantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        this.cantidad = nuevaCantidad;
    }
}
