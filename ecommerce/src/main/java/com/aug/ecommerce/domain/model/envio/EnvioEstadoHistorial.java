package com.aug.ecommerce.domain.model.envio;

import java.time.Instant;
import java.util.Objects;

public class EnvioEstadoHistorial {
    private final Long id;
    private final EstadoEnvio estadoEnvio;
    private final String observacion;
    private final Instant fechaCambio;

    //EnvioEstadoHistorial Nuevo
    static EnvioEstadoHistorial create(EstadoEnvio estadoEnvio) {
        return create(estadoEnvio, null);
    }

    //EnvioEstadoHistorial Cambio de estado
    static EnvioEstadoHistorial create(EstadoEnvio estadoEnvio, String observacion) {
        return new EnvioEstadoHistorial(null, estadoEnvio, observacion, null);
    }

    //EnvioEstadoHistorial seteado desde BD
    static EnvioEstadoHistorial fromPersistence(
            Long idEstadoHistorial, EstadoEnvio estadoEnvio, String observacion, Instant fechaCambio) {
        if (idEstadoHistorial == null) throw new IllegalArgumentException("El idEstadoHistorial no puede ser nulo");
        return new EnvioEstadoHistorial(idEstadoHistorial, estadoEnvio, observacion, fechaCambio);
    }

    private EnvioEstadoHistorial(Long id, EstadoEnvio estadoEnvio, String observacion, Instant fechaCambio) {
        this.id = id;
        this.estadoEnvio = Objects.requireNonNull(estadoEnvio, "El estado no puede ser nulo");
        this.observacion = observacion;
        this.fechaCambio = fechaCambio;
    }

    public Long getId() {
        return id;
    }

    public EstadoEnvio getEstadoEnvio() {
        return estadoEnvio;
    }

    public String getObservacion() {
        return observacion;
    }

    public Instant getFechaCambio() {
        return fechaCambio;
    }


}
