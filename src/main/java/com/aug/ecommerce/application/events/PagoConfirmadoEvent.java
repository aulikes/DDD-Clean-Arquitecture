package com.aug.ecommerce.application.events;

import java.io.Serializable;
import java.time.Instant;

public record PagoConfirmadoEvent(
        Long ordenId, Long pagoId, Instant fecha, boolean exitoso,
        String codigoTransaccion, String mensajeError) implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "pago.orden.confirmado";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}