package com.aug.ecommerce.application.command;

import java.util.Set;

public record CrearProductoCommand(
        String nombre,
        String descripcion,
        double precio,
        String imagenUrl,
        Long categoriaId
) {}
