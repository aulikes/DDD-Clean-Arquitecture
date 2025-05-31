package com.aug.ecommerce.application.event;

public record EnvioRequestedEvent(Long ordenId, String direccionEnvio) {
}
