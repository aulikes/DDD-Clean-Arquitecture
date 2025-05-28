package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.command.RealizarOrdenCommand;
import com.aug.ecommerce.domain.model.orden.EstadoOrden;
import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.model.producto.Producto;
import com.aug.ecommerce.domain.model.inventario.Inventario;
import com.aug.ecommerce.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    public Long crearOrden(RealizarOrdenCommand command) {
        // Validar cliente
        if (clienteRepository.findById(command.getClienteId()).isEmpty()) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        Orden orden = Orden.create(command.getClienteId(), command.getDireccionEnviar());

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

    public void pagarOrden(Long idOrden){
        ordenRepository.findById(idOrden).ifPresentOrElse(orden -> {
            if (orden.getEstado() == EstadoOrden.NUEVA) {
                orden.pagar();
                ordenRepository.save(orden);
                log.info("Orden {} actualizada a estado PAGADA", orden.getId());
            } else {
                log.error("La orden {} no está en estado NUEVA. No se puede marcar como PAGADA.", orden.getId());
            }
        }, () -> log.error("Orden con ID {} no encontrada", idOrden));

    }

    //FALTARÍA IMPLEMENTAR
//    public void agregarItem(Orden orden, Long productoId, int cantidad, double precioUnitario) {
//        orden.agregarItem(productoId, cantidad, precioUnitario);
//    }
//
//    public void removerItem(Orden orden, Long itemOrdenId) {
//        orden.removerItem(itemOrdenId);
//    }
//
//    public void cambiarCantidadItem(Orden orden, Long itemOrdenId, int nuevaCantidad) {
//        orden.cambiarCantidadItem(itemOrdenId, nuevaCantidad);
//    }
//
//    public double calcularTotal(Orden orden) {
//        return orden.calcularTotal();
//    }
//
//    public BOOLEAN cancelarOrden(Orden orden) {
//        orden.cancelar();
//        return new OrdenCancelada(orden.getId());
//    }
}
