package com.aug.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "INVENTARIO")
public class InventarioEntity {

    @Id
    private Long productoId;

    @Column(nullable = false)
    private Long cantidadDisponible;
}
