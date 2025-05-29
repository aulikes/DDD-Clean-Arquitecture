package com.aug.ecommerce.application.command;

public record CrearProductoCommand(
        String nombre,
        String descripcion,
        double precio,
        String imagenUrl,
        Long cantidad,
        Long categoriaId
) {}
