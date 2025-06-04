package com.aug.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "ENVIO")
public class EnvioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @NotNull
    @Column(nullable = false, name = "direccion_envio")
    private String direccionEnvio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    @Column(name = "trackingNumber")
    private String trackingNumber;

    @Column(name = "intentos")
    private int intentos;

    @Column(name = "razon_fallo")
    private String razonFallo;
    private String trackingNumber;

    public enum Estado {
        PENDIENTE, PREPARANDO, DESPACHADO, ENTREGADO, FALLIDO
    }
}
