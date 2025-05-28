package com.aug.ecommerce.application.command;

import lombok.Data;

import java.util.List;

@Data
public class RealizarOrdenCommand {
    private final Long clienteId;
    private final List<Item> items;
    private String direccionEnviar;

    public RealizarOrdenCommand(Long clienteId, List<Item> items, String direccionEnviar) {
        this.clienteId = clienteId;
        this.items = items;
        this.direccionEnviar = direccionEnviar;

    }

    @Data
    public static class Item {
        private final Long productoId;
        private final int cantidad;

        public Item(Long productoId, int cantidad) {
            this.productoId = productoId;
            this.cantidad = cantidad;
        }
    }
}
