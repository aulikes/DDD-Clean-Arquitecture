package com.aug.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "CLIENTE")
public class ClienteEntity {

    @Id
    private UUID id;

    private String nombre;
    private String email;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cliente_id") // FK en direcciones
    private List<DireccionEntity> direcciones = new ArrayList<>();
}
