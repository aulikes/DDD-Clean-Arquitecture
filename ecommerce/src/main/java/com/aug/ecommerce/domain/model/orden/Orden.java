package com.aug.ecommerce.domain.model.orden;

import com.aug.ecommerce.domain.model.pago.EstadoPago;
import com.aug.ecommerce.domain.model.pago.Pago;

import java.util.*;

public class Orden {
    private final Long id;
    private final Long clienteId;
    private String direccionEnviar;
    private final List<ItemOrden> items;
    private EstadoOrden estado;

    //Orden Nuevo
    public static Orden create(Long clienteId, String direccionEnviar) {
        return new Orden(null, clienteId, direccionEnviar, new ArrayList<>(), EstadoOrden.NUEVA);
    }

    //Orden seteado desde BD
    public static Orden fromPersistence(Long ordenId, Long clienteId, String direccionEnviar,
                                        List<ItemOrden> items, EstadoOrden estado) {
        if (ordenId == null) throw new IllegalArgumentException("El ordenId no puede ser nulo");
        return new Orden(ordenId, clienteId, direccionEnviar, items, estado);
    }

    private Orden(Long id, Long clienteId, String direccionEnviar, List<ItemOrden> items, EstadoOrden estado) {
        this.id = id;
        this.clienteId = Objects.requireNonNull(clienteId, "El clienteId no puede ser nulo");
        this.direccionEnviar = Objects.requireNonNull(direccionEnviar, "La direccionEnviar no puede ser nula");
        this.items = items;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public List<ItemOrden> getItems() { return Collections.unmodifiableList(items); }
    public EstadoOrden getEstado() { return estado; }
    public String getDireccionEnviar() { return direccionEnviar; }

    public void agregarItem(Long productoId, int cantidad, double precioUnitario) {
        validarEstadoEditable();
        items.add(ItemOrden.create(productoId, cantidad, precioUnitario));
    }

    //Cuando se Mapea desde la Infra
    public void restoreItem(Long itemOrdenId, Long productoId, int cantidad, double precioUnitario) {
        if (this.id == null) throw new IllegalArgumentException("El ordenId no puede ser nulo");
        items.add(ItemOrden.fromPersistence(itemOrdenId, productoId, cantidad, precioUnitario));
    }

    public void removerItem(Long itemOrdenId) {
        validarEstadoEditable();
        items.removeIf(item -> item.getId().equals(itemOrdenId));
    }

    public void cambiarDireccion(String direccion) {
        validarEstadoEditable();
        this.direccionEnviar = direccion;
    }

    public void cambiarCantidadItem(Long itemOrdenId, int nuevaCantidad) {
        validarEstadoEditable();
        items.stream()
                .filter(item -> item.getId().equals(itemOrdenId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Ítem no encontrado"))
                .cambiarCantidad(nuevaCantidad);
    }

    public double calcularTotal() {
        validarOrdenTieneItems();
        double total = items.stream().mapToDouble(ItemOrden::getSubtotal).sum();
        if (total == 0)
            throw new IllegalStateException("No se puede procesar una orden con total 0");
        return total;
    }

    public void reservar() {
        cambiarEstado(EstadoOrden.PENDIENTE);
    }

    public void pagar() {
        cambiarEstado(EstadoOrden.PAGADA);
    }

    public void enviar() {
        cambiarEstado(EstadoOrden.ENVIADA);
    }

    public void entregar() {
        cambiarEstado(EstadoOrden.ENTREGADA);
    }

    public void cancelar() {
        cambiarEstado(EstadoOrden.CANCELADA);
    }

    private void cambiarEstado(EstadoOrden nuevoEstado) {
        estado.validarTransicionA(nuevoEstado);
        this.estado = nuevoEstado;
    }

    private void validarEstadoEditable() {
        if (!estado.equals(EstadoOrden.NUEVA))
            throw new IllegalStateException("No se puede modificar una orden que no está en estado NUEVA");
    }

    private void validarOrdenTieneItems() {
        if (items.isEmpty())
            throw new IllegalStateException("No se puede procesar una orden sin ítems");
    }
}
