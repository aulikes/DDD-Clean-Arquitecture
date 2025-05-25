package com.aug.biblioteca.app.dominio;

public class Ejemplar {

    private final String id;
    private final String libroId;
    private EstadoEjemplar estado;
    private String ubicacionFisica;

    public Ejemplar(String id, String libroId, String ubicacionFisica) {
        this.id = id;
        this.libroId = libroId;
        this.ubicacionFisica = ubicacionFisica;
        this.estado = EstadoEjemplar.DISPONIBLE;
    }

    public String getId() {
        return id;
    }

    public String getLibroId() {
        return libroId;
    }

    public String getUbicacionFisica() {
        return ubicacionFisica;
    }

    public EstadoEjemplar getEstado() {
        return estado;
    }

    // Comportamiento: Marcar ejemplar como prestado
    public void marcarPrestado() {
        if (!estado.equals(EstadoEjemplar.DISPONIBLE)) {
            throw new IllegalStateException("El ejemplar no está disponible para préstamo.");
        }
        this.estado = EstadoEjemplar.PRESTADO;
    }

    // Comportamiento: Marcar ejemplar como disponible
    public void marcarDisponible() {
        if (estado.equals(EstadoEjemplar.DISPONIBLE)) {
            throw new IllegalStateException("El ejemplar ya está disponible.");
        }
        this.estado = EstadoEjemplar.DISPONIBLE;
    }

    // Comportamiento: Marcar como dañado (por ejemplo, al devolverse roto)
    public void marcarDañado() {
        this.estado = EstadoEjemplar.DANIADO;
    }

    // Comportamiento: ¿Está disponible?
    public boolean estaDisponible() {
        return estado.equals(EstadoEjemplar.DISPONIBLE);
    }

//    // (Opcional) Cambio de ubicación
//    public void cambiarUbicacion(String nuevaUbicacion) {
//        this.ubicacionFisica = nuevaUbicacion;
//    }
}
