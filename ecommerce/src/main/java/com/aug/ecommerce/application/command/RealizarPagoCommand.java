package com.aug.ecommerce.application.command;

public record RealizarPagoCommand(Long ordenId, String direccionEntrega, Double monto, String medioPago) {}

