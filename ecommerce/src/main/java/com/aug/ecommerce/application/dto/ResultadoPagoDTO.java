package com.aug.ecommerce.application.dto;

public record ResultadoPagoDTO(
        boolean exitoso,
        String codigoTransaccion,
        String mensaje
) {}
