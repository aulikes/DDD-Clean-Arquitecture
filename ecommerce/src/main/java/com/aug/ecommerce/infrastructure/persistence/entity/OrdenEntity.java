package com.aug.ecommerce.infrastructure.persistence.entity;

import com.aug.ecommerce.domain.model.orden.EstadoOrden;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "ORDEN")
public class OrdenEntity {

    @Id
    private UUID id;

    private UUID clienteId;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estado;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "orden_id")
    private List<ItemOrdenEntity> items;

}

