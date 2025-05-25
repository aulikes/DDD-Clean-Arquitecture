package com.aug.biblioteca.app.dominio;

import java.time.LocalDate;
import java.util.UUID;

public class Multa {

    private final String id;
    private final String prestamoId;
    private final String usuarioId;
    private final long monto;
    private final LocalDate fechaGeneracion;

    private boolean pagada;
    private LocalDate fechaPago;
    private String referenciaPago;

    public Multa(String prestamoId, String usuarioId, long monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto de la multa debe ser mayor que cero.");
        }

        this.id = UUID.randomUUID().toString();
        this.prestamoId = prestamoId;
        this.usuarioId = usuarioId;
        this.monto = monto;
        this.fechaGeneracion = LocalDate.now();
        this.pagada = false;
    }

    public void pagar(String referenciaPago, LocalDate fechaPago) {
        if (this.pagada) {
            throw new IllegalStateException("La multa ya fue pagada.");
        }
        if (fechaPago.isBefore(fechaGeneracion)) {
            throw new IllegalArgumentException("La fecha de pago no puede ser anterior a la fecha de generación.");
        }

        this.pagada = true;
        this.fechaPago = fechaPago;
        this.referenciaPago = referenciaPago;
    }

    public boolean estaPagada() {
        return pagada;
    }

    public boolean esVencida(LocalDate fechaLimitePago) {
        return !pagada && LocalDate.now().isAfter(fechaLimitePago);
    }

    public String getId() {
        return id;
    }

    public String getPrestamoId() {
        return prestamoId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public long getMonto() {
        return monto;
    }

    public boolean isPagada() {
        return pagada;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }
}
