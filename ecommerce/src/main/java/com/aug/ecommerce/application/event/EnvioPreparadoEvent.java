package com.aug.ecommerce.application.event;

import java.io.Serializable;
import java.time.Instant;

public record EnvioPreparadoEvent(
        Long ordenId, Long envioId, Instant fecha, boolean exitoso,
        String codigoTransaccion, String mensajeError) implements IntegrationEvent, Serializable {

    @Override
    public String getEventType() {
        return "envio.orden.preparado";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}