package com.aug.ecommerce.application.dto;

public record ResultadoEnvioDTO(
        boolean exitoso,
        String trackingNumber,
        String estado,
        String mensaje) {
}
