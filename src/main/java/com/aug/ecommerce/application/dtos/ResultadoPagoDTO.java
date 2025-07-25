package com.aug.ecommerce.application.dtos;

public record ResultadoPagoDTO(
        boolean exitoso,
        String codigoTransaccion,
        String mensaje
) {}
