package com.aug.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "INVENTARIO")
public class InventarioEntity {

    @Id
    private UUID id;

    private UUID productoId;
    private int cantidadDisponible;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }

    public int getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(int cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
}
