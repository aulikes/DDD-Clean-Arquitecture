package com.aug.ecommerce.application.event;

import java.io.Serializable;
import java.util.List;

/**
 * Evento que se publica cuando se crea una orden y debe validarse.
 * Es consumido por los servicios de clientes, inventario y productos.
 */
public record OrdenCreadaEvent(Long ordenId, Long clienteId, String direccion,
                               List<ItemOrdenCreada> items) implements IntegrationEvent, Serializable {

    public record ItemOrdenCreada(Long productoId, int cantidad) {
    }

    @Override
    public String getEventType() {
        return "orden.multicast.creada";
    }

    @Override
    public String getVersion() {
        return "v1";
    }
}
