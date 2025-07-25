package com.aug.ecommerce.application.commands;

public record CrearInventarioCommand(
        Long productoId,
        Long cantidad
) { }
