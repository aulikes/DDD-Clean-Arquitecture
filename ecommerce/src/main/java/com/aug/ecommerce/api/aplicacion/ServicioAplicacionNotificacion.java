package com.aug.ecommerce.api.aplicacion;

import com.aug.ecommerce.api.dominio.entidades.notificacion.Notificacion;

public class ServicioAplicacionNotificacion {

    public void marcarComoEnviada(Notificacion notificacion) {
        notificacion.marcarComoEnviada();
    }

    public Notificacion crearNotificacionPendiente(
            java.util.UUID id,
            java.util.UUID destinatarioId,
            String tipo,
            String mensaje
    ) {
        return new Notificacion(id, destinatarioId, tipo, mensaje);
    }
}
