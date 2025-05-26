package com.aug.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "PRODUCTO")
public class ProductoEntity {

    @Id
    private UUID id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagenUrl;
    private Set<UUID> categoriasIds;
}
