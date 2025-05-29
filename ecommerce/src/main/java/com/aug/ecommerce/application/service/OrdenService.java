package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.command.RealizarOrdenCommand;
import com.aug.ecommerce.application.event.OrderPaymentRequestedEvent;
import com.aug.ecommerce.application.publisher.OrderEventPublisher;
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
    private final OrderEventPublisher publisher;

    public Long crearOrden(RealizarOrdenCommand command) {
        // Validar cliente
        if (clienteRepository.findById(command.getClienteId()).isEmpty()) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
        Orden orden = Orden.create(command.getClienteId(), command.getDireccionEnviar());
        for (RealizarOrdenCommand.Item item : command.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getProductoId()));

            Inventario inventario = inventarioRepository.findById(producto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado: " + producto.getId()));

            if (inventario.getStockDisponible() < item.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para producto: " + producto.getNombre());
            }
            orden.agregarItem(producto.getId(), item.getCantidad(), producto.getPrecio());
        }
        ordenRepository.save(orden);
        return orden.getId();
    }

    /**
     * Solicita el inicio del proceso de pago para una orden.
     * Publica un evento al exterior con la intención de realizar el pago.
     */
    public void solicitarPago(Long idOrden, String medioPago){
        ordenRepository.findById(idOrden).ifPresentOrElse(orden -> {
            orden.reservar();
            ordenRepository.save(orden);
            log.info("Orden {} actualizada a estado PENDIENTE", orden.getId());

            // Publicar evento
            OrderPaymentRequestedEvent event = new OrderPaymentRequestedEvent(
                    orden.getId(),
                    orden.getDireccionEnviar(),
                    orden.calcularTotal(),
                    medioPago
            );

            publisher.publishOrderPaymentRequested(event);
            log.info("Evento OrderPaymentRequestEvent publicado para orden {}", orden.getId());
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
