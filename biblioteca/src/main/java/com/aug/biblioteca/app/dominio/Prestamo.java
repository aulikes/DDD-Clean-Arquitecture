package com.aug.biblioteca.app.dominio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Prestamo {

    private final String id;
    private final String usuarioId;
    private final String ejemplarId;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;

    private LocalDate fechaDevolucion;

    private Prestamo(String id, String usuarioId, String ejemplarId, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.ejemplarId = ejemplarId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public static Prestamo crear(String usuarioId, String ejemplarId, int diasDuracion) {
        if (diasDuracion <= 0) {
            throw new IllegalArgumentException("La duración del préstamo debe ser mayor que cero.");
        }

        LocalDate hoy = LocalDate.now();
        LocalDate fechaFin = hoy.plusDays(diasDuracion);

        return new Prestamo(
                UUID.randomUUID().toString(),
                usuarioId,
                ejemplarId,
                hoy,
                fechaFin
        );
    }

    // Registra la fecha de devolución
    public void registrarDevolucion(LocalDate fechaDevolucion) {
        if (this.fechaDevolucion != null) {
            throw new IllegalStateException("Este préstamo ya fue devuelto.");
        }
        if (fechaDevolucion.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La devolución no puede ser antes del inicio.");
        }
        this.fechaDevolucion = fechaDevolucion;
    }

    // ¿Fue devuelto?
    public boolean fueDevuelto() {
        return fechaDevolucion != null;
    }

    // ¿Está vencido en este momento?
    public boolean requiereMulta() {
        return !fueDevuelto() && LocalDate.now().isAfter(fechaFin);
    }

    // Calcula el monto de multa acumulado hasta hoy
    public long calcularMontoMulta(long valorPorDia) {
        if (!requiereMulta()) return 0;
        long diasMora = ChronoUnit.DAYS.between(fechaFin, LocalDate.now());
        return diasMora * valorPorDia;
    }

    public String getId() {
        return id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getEjemplarId() {
        return ejemplarId;
    }

}

