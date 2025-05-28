package com.aug.ecommerce.application.event;

import java.time.Instant;

public record PagoConfirmadoEvent(Long ordenId, String direccionEntrega, Instant fecha) {}