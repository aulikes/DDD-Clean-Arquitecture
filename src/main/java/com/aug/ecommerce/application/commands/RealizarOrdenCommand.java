package com.aug.ecommerce.application.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RealizarOrdenCommand {
    private final Long clienteId;
    private final List<Item> items;
    private String direccionEnviar;

    @Data
    @AllArgsConstructor
    public static class Item {
        private final Long productoId;
        private final int cantidad;
        private final double precioUnitario;
    }
}
