package com.aug.ecommerce.infrastructure.persistence.entity;

import com.aug.ecommerce.infrastructure.persistence.entity.enums.EstadoEnvioEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@Table(name = "ENVIO_ESTADO_HISTORIAL")
public class EnvioEstadoHistorialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "envio_id", nullable = false)
    private EnvioEntity envio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoEnvioEntity estado;

    @Column(name = "fecha_cambio", nullable = false, updatable = false)
    private Instant fechaCambio;

    @Column(name = "observacion")
    private String observacion;

    @PrePersist
    public void prePersist() {
        this.fechaCambio = Instant.now();
    }
}

