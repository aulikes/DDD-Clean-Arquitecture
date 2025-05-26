package com.aug.ecommerce.application.command;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RealizarOrdenCommand {
    private final UUID clienteId;
    private final List<Item> items;

    public RealizarOrdenCommand(UUID clienteId, List<Item> items) {
        this.clienteId = clienteId;
        this.items = items;
    }

    @Data
    public static class Item {
        private final UUID productoId;
        private final int cantidad;

        public Item(UUID productoId, int cantidad) {
            this.productoId = productoId;
            this.cantidad = cantidad;
        }
    }
}
