package com.aug.ecommerce.infrastructure.persistence.entity;

import com.aug.ecommerce.domain.model.orden.EstadoOrden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "ORDEN")
public class OrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long clienteId;

    @NotNull
    @Column(nullable = false)
    private String estado;

    @NotNull
    @Column(nullable = false)
    private String direccionEnviar;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "orden_id")
    private List<ItemOrdenEntity> items;

}

