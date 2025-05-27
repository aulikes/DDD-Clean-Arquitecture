package com.aug.ecommerce.domain.model.notificacion;

import java.util.Objects;
import java.util.UUID;

public class Notificacion {
    public enum Estado {
        PENDIENTE, ENVIADA
    }

    private final Long id;
    private final Long destinatarioId;
    private final String tipo;
    private final String mensaje;
    private Estado estado;

    public Notificacion(Long id, Long destinatarioId, String tipo, String mensaje) {
        this.id = id;
        this.destinatarioId = Objects.requireNonNull(destinatarioId, "El destinatario no puede ser nulo");
        this.tipo = Objects.requireNonNull(tipo, "El tipo no puede ser nulo");
        this.mensaje = Objects.requireNonNull(mensaje, "El mensaje no puede ser nulo");
        this.estado = Estado.PENDIENTE;
    }

    public Long getId() { return id; }
    public Long getDestinatarioId() { return destinatarioId; }
    public String getTipo() { return tipo; }
    public String getMensaje() { return mensaje; }
    public Estado getEstado() { return estado; }

    public void marcarComoEnviada() {
        this.estado = Estado.ENVIADA;
    }
}
