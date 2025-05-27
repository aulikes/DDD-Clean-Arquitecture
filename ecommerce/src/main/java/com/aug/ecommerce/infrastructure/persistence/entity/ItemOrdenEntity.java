package com.aug.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ITEM_ORDEN")
public class ItemOrdenEntity {

    @Id
    private Long id;

    private Long productoId;
    private int cantidad;
    private double precioUnitario;

}

