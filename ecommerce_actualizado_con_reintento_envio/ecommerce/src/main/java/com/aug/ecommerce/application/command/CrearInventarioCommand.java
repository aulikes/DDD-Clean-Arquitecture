package com.aug.ecommerce.application.command;

public record CrearInventarioCommand(
        Long productoId,
        Long cantidad
) { }
