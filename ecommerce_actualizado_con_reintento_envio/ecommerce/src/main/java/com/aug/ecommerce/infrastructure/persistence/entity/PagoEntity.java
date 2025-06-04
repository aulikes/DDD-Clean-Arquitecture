package com.aug.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "PAGO")
public class PagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @NotNull
    @Column(nullable = false)
    private double monto;

    @NotNull
    @Column(name = "medio_pago", nullable = false)
    private String metodo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    @Column(name = "codigo_transaccion")
    private String codigoTransaccion;

    @Column(name = "mensaje_error")
    private String mensajeError;

    public enum Estado {
        PENDIENTE, CONFIRMADO, FALLIDO
    }
}
