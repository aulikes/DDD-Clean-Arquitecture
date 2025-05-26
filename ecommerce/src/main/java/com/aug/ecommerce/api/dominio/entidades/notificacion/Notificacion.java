package com.aug.ecommerce.api.dominio.entidades.notificacion;

import java.util.Objects;
import java.util.UUID;

public class Notificacion {
    public enum Estado {
        PENDIENTE, ENVIADA
    }

    private final UUID id;
    private final UUID destinatarioId;
    private final String tipo;
    private final String mensaje;
    private Estado estado;

    public Notificacion(UUID id, UUID destinatarioId, String tipo, String mensaje) {
        this.id = Objects.requireNonNull(id, "El id no puede ser nulo");
        this.destinatarioId = Objects.requireNonNull(destinatarioId, "El destinatario no puede ser nulo");
        this.tipo = Objects.requireNonNull(tipo, "El tipo no puede ser nulo");
        this.mensaje = Objects.requireNonNull(mensaje, "El mensaje no puede ser nulo");
        this.estado = Estado.PENDIENTE;
    }

    public UUID getId() { return id; }
    public UUID getDestinatarioId() { return destinatarioId; }
    public String getTipo() { return tipo; }
    public String getMensaje() { return mensaje; }
    public Estado getEstado() { return estado; }

    public void marcarComoEnviada() {
        this.estado = Estado.ENVIADA;
    }
}
