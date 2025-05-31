package com.aug.ecommerce.application.event;

import java.time.Instant;

public record EnvioPreparadoEvent(
        Long ordenId, Long envioId, Instant fecha, boolean exitoso,
        String codigoTransaccion, String mensajeError) {}