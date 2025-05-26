package com.aug.ecommerce.api.aplicacion;

import com.aug.ecommerce.api.dominio.entidades.orden.Orden;
import com.aug.ecommerce.api.dominio.eventos.OrdenCancelada;

import java.util.UUID;

public class ServicioAplicacionOrden {

    public Orden crearOrden(UUID id, UUID clienteId) {
        return new Orden(id, clienteId);
    }

    public void agregarItem(Orden orden, UUID productoId, int cantidad, double precioUnitario) {
        orden.agregarItem(productoId, cantidad, precioUnitario);
    }

    public void removerItem(Orden orden, UUID itemOrdenId) {
        orden.removerItem(itemOrdenId);
    }

    public void cambiarCantidadItem(Orden orden, UUID itemOrdenId, int nuevaCantidad) {
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
