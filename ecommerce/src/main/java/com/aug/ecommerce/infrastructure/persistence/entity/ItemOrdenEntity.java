package com.aug.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ITEM_ORDEN")
public class ItemOrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private OrdenEntity orden;

    @Column(name = "producto_id", nullable = false)
    private Long productoId; //No se hace la FK, simulando que van a estar en micros diferentes

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "precioUnitario", nullable = false)
    private double precioUnitario;

}

