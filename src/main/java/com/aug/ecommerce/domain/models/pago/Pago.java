package com.aug.ecommerce.domain.models.pago;

import java.util.Objects;

public class Pago {

    private final Long id;
    private final Long ordenId;
    private final double monto;
    private final String metodo; // Ej: "tarjeta", "paypal", etc.
    private EstadoPago estado;
    private String codigoTransaccion;
    private String mensajeError;

    //Pago Nuevo
    public static Pago create(Long ordenId, double monto, String metodo) {
        return new Pago(null, ordenId, monto, metodo, EstadoPago.PENDIENTE, null, null);
    }

    //Pago seteado desde BD
    public static Pago fromPersistence(Long id, Long ordenId, double monto, String metodo,
                                        EstadoPago estado, String codigoTransaccion, String mensajeError) {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        return new Pago(id, ordenId, monto, metodo, estado, codigoTransaccion, mensajeError);
    }

    // Constructor privado
    private Pago(Long id, Long ordenId, double monto, String metodo,
                 EstadoPago estado, String codigoTransaccion, String mensajeError) {

        this.id = id; // puede ser null en caso de nuevo pago
        this.ordenId = Objects.requireNonNull(ordenId, "El ordenId no puede ser nulo");
        if (monto <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        this.monto = monto;
        this.metodo = Objects.requireNonNull(metodo, "El método no puede ser nulo");
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
        this.codigoTransaccion = codigoTransaccion;
        this.mensajeError = mensajeError;
    }

    public Long getId() { return id; }
    public Long getOrdenId() { return ordenId; }
    public double getMonto() { return monto; }
    public String getMetodo() { return metodo; }
    public EstadoPago getEstado() { return estado; }
    public String getCodigoTransaccion() { return codigoTransaccion; }
    public String getMensajeError() { return mensajeError; }

    public void confirmar(String codigoTransaccion) {
        if (estado != EstadoPago.PENDIENTE)
            throw new IllegalStateException("El pago ya fue procesado");
        this.estado = EstadoPago.CONFIRMADO;
        this.codigoTransaccion = codigoTransaccion;
    }

    public void fallar(String mensajeError) {
        if (estado != EstadoPago.PENDIENTE)
            throw new IllegalStateException("El pago ya fue procesado");
        this.estado = EstadoPago.FALLIDO;
        this.mensajeError = mensajeError;
    }
}
