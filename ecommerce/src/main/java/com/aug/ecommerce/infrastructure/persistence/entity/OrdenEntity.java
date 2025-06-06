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

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemOrdenEntity> items;

    @NotNull
    @Column(nullable = false)
    private Long clienteId; //No se hace la FK, simulando que van a estar en micros diferentes

    @NotNull
    @Column(nullable = false)
    private String estado;

    @NotNull
    @Column(nullable = false)
    private String direccionEnviar;

    private String error;

}

