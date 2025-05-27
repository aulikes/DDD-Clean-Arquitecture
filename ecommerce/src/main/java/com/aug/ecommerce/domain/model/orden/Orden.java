package com.aug.ecommerce.domain.model.orden;

import java.util.*;

public class Orden {
    private final Long id;
    private final Long clienteId;
    private final List<ItemOrden> items;
    private EstadoOrden estado;

    public Orden(Long id, Long clienteId) {
        this.id = id;
        this.clienteId = Objects.requireNonNull(clienteId, "El clienteId no puede ser nulo");
        this.items = new ArrayList<>();
        this.estado = EstadoOrden.NUEVA;
    }

    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public List<ItemOrden> getItems() { return Collections.unmodifiableList(items); }
    public EstadoOrden getEstado() { return estado; }

    public void agregarItem(Long productoId, int cantidad, double precioUnitario) {
        validarEstadoEditable();
        items.add(new ItemOrden(null, productoId, cantidad, precioUnitario));
    }

    public void removerItem(Long itemOrdenId) {
        validarEstadoEditable();
        items.removeIf(item -> item.getId().equals(itemOrdenId));
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
        return items.stream().mapToDouble(ItemOrden::getSubtotal).sum();
    }

    public void pagar() {
        validarOrdenTieneItems();
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
