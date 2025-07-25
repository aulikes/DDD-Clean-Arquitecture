package com.aug.ecommerce.application.dtos;

public record ResultadoEnvioDTO(
        boolean exitoso,
        String trackingNumber,
        String estado,
        String mensaje) {
}
