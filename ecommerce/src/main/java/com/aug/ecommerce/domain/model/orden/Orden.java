package com.aug.ecommerce.domain.model.orden;

import java.util.*;

public class Orden {
    private final UUID id;
    private final UUID clienteId;
    private final List<ItemOrden> items;
    private EstadoOrden estado;

    public Orden(UUID id, UUID clienteId) {
        this.id = Objects.requireNonNull(id, "El id no puede ser nulo");
        this.clienteId = Objects.requireNonNull(clienteId, "El clienteId no puede ser nulo");
        this.items = new ArrayList<>();
        this.estado = EstadoOrden.NUEVA;
    }

    public UUID getId() { return id; }
    public UUID getClienteId() { return clienteId; }
    public List<ItemOrden> getItems() { return Collections.unmodifiableList(items); }
    public EstadoOrden getEstado() { return estado; }

    public void agregarItem(UUID productoId, int cantidad, double precioUnitario) {
        validarEstadoEditable();
        items.add(new ItemOrden(UUID.randomUUID(), productoId, cantidad, precioUnitario));
    }

    public void removerItem(UUID itemOrdenId) {
        validarEstadoEditable();
        items.removeIf(item -> item.getId().equals(itemOrdenId));
    }

    public void cambiarCantidadItem(UUID itemOrdenId, int nuevaCantidad) {
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
