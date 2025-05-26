package com.aug.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "DIRECCION")
public class DireccionEntity {

    @Id
    private UUID id;

    private String calle;
    private String ciudad;
    private String pais;
    private String codigoPostal;
}
