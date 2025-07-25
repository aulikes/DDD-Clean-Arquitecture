package com.aug.ecommerce.infrastructure.persistence.entity;

import com.aug.ecommerce.infrastructure.persistence.entity.enums.EstadoEnvioEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "ENVIO")
public class EnvioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @Column(nullable = false, name = "direccion_envio")
    private String direccionEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEnvioEntity estado;

    @Column(name = "trackingNumber")
    private String trackingNumber;

    @Column(name = "intentos")
    private int intentos;

    @Column(name = "razon_fallo")
    private String razonFallo;

    @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EnvioEstadoHistorialEntity> historial;
}
