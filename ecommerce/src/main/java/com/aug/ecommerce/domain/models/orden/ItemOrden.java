package com.aug.ecommerce.domain.models.orden;

import java.util.Objects;

public final class ItemOrden {
    private final Long id;
    private final Long productoId;
    private int cantidad;
    private final double precioUnitario;

    //Item Nuevo
    static ItemOrden create(Long productoId, int cantidad, double precioUnitario) {
        return new ItemOrden(null, productoId, cantidad, precioUnitario);
    }

    //Item seteado desde BD
    static ItemOrden fromPersistence(Long itemId, Long productoId, int cantidad, double precioUnitario) {
        if (itemId == null) throw new IllegalArgumentException("El itemId no puede ser nulo");
        return new ItemOrden(itemId, productoId, cantidad, precioUnitario);
    }

    private ItemOrden(Long itemId, Long productoId, int cantidad, double precioUnitario) {
        this.id = itemId;
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
