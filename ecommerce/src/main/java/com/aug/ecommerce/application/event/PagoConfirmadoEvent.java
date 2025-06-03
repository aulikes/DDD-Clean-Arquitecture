package com.aug.ecommerce.application.event;

import java.io.Serializable;
import java.time.Instant;

public record PagoConfirmadoEvent(
        Long ordenId, Long pagoId, Instant fecha, boolean exitoso,
        String codigoTransaccion, String mensajeError) implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "pago.confirmado";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}