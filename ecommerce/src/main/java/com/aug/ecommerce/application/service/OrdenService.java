package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.command.RealizarOrdenCommand;
import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.model.producto.Producto;
import com.aug.ecommerce.domain.model.inventario.Inventario;
import com.aug.ecommerce.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    public UUID crearOrden(RealizarOrdenCommand command) {
        // Validar cliente
        if (!clienteRepository.findById(command.getClienteId()).isPresent()) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        Orden orden = new Orden(UUID.randomUUID(), command.getClienteId());

        for (RealizarOrdenCommand.Item item : command.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getProductoId()));

            Inventario inventario = inventarioRepository.findByProductoId(producto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado: " + producto.getId()));

            if (inventario.getStockDisponible() < item.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para producto: " + producto.getNombre());
            }

            orden.agregarItem(producto.getId(), item.getCantidad(), producto.getPrecio());
        }

        ordenRepository.save(orden);
        return orden.getId();
    }
}
