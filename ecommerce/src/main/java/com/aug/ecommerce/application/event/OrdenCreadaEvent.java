package com.aug.ecommerce.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * Evento que se publica cuando se crea una orden y debe validarse.
 * Es consumido por los servicios de clientes, inventario y productos.
 */
@Getter
@AllArgsConstructor
public class OrdenCreadaEvent implements IntegrationEvent, Serializable {

    private final Long ordenId;
    private final Long clienteId;
    private final String direccion;
    private final List<ItemOrdenCreada> items;

    @Getter
    @AllArgsConstructor
    public static class ItemOrdenCreada {
        private final Long productoId;
        private final int cantidad;
    }

    @Override
    public String getEventType() {
        return "orden.creada";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
