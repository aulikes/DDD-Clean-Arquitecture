package com.aug.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "ITEMS_ORDEN")
public class ItemOrdenEntity {

    @Id
    private UUID id;

    private UUID productoId;
    private int cantidad;
    private double precioUnitario;

}

