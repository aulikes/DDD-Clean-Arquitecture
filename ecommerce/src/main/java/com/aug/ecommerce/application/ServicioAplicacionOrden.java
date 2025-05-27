package com.aug.ecommerce.application;

import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.event.OrdenCancelada;

import java.util.UUID;

public class ServicioAplicacionOrden {

    public Orden crearOrden(Long id, Long clienteId) {
        return new Orden(id, clienteId);
    }

    public void agregarItem(Orden orden, Long productoId, int cantidad, double precioUnitario) {
        orden.agregarItem(productoId, cantidad, precioUnitario);
    }

    public void removerItem(Orden orden, Long itemOrdenId) {
        orden.removerItem(itemOrdenId);
    }

    public void cambiarCantidadItem(Orden orden, Long itemOrdenId, int nuevaCantidad) {
        orden.cambiarCantidadItem(itemOrdenId, nuevaCantidad);
    }

    public double calcularTotal(Orden orden) {
        return orden.calcularTotal();
    }

    public OrdenCancelada cancelarOrden(Orden orden) {
        orden.cancelar();
        return new OrdenCancelada(orden.getId());
    }
}
