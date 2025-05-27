package com.aug.ecommerce.application;

import com.aug.ecommerce.domain.model.notificacion.Notificacion;

public class ServicioAplicacionNotificacion {

    public void marcarComoEnviada(Notificacion notificacion) {
        notificacion.marcarComoEnviada();
    }

    public Notificacion crearNotificacionPendiente(
            Long id,
            Long destinatarioId,
            String tipo,
            String mensaje
    ) {
        return new Notificacion(id, destinatarioId, tipo, mensaje);
    }
}
