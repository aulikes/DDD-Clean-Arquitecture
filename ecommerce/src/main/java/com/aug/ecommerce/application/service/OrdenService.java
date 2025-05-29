package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.command.RealizarOrdenCommand;
import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.event.OrderPaymentRequestedEvent;
import com.aug.ecommerce.application.publisher.OrderEventPublisher;
import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final OrderEventPublisher publisher;

    public Long crearOrden(RealizarOrdenCommand command) {
        //Creamos la Orden
        Orden orden = Orden.create(command.getClienteId(), command.getDireccionEnviar());
        //Adicionamos Items a la Orden
        for (RealizarOrdenCommand.Item item : command.getItems()) {
            orden.agregarItem(item.getProductoId(), item.getCantidad(), item.getPrecioUnitario());
        }
        this.guardarYEnviarAValidarOrden(orden);
        return orden.getId();
    }

    public void marcarOrdenValidada(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + ordenId));
        orden.marcarListaParaPago();
        ordenRepository.save(orden);
        log.debug("Orden {} marcada como LISTA_PARA_PAGO", ordenId);
    }

    public void marcarOrdenFallida(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + ordenId));
        orden.marcarValidacionFallida();
        ordenRepository.save(orden);
        log.debug("Orden {} marcada como VALIDACION_FALLIDA", ordenId);
    }

    public void reenviarOrdenAValidacion(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + ordenId));
        guardarYEnviarAValidarOrden(orden);
    }

    public void iniciarPago(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + ordenId));
        orden.iniciarPago();
        ordenRepository.save(orden);
        log.debug("Orden {} pasó a estado PAGO_EN_PROCESO", ordenId);

        // Aquí puedes emitir OrderPaymentRequestedEvent si deseas
    }

    /**
     * Solicita el inicio del proceso de pago para una orden.
     * Publica un evento al exterior con la intención de realizar el pago.
     */
    public void solicitarPago(Long idOrden, String medioPago){
        ordenRepository.findById(idOrden).ifPresentOrElse(orden -> {
            orden.iniciarPago();
            ordenRepository.save(orden);
            log.debug("Orden {} actualizada a estado PAGO_EN_PROCESO", orden.getId());

            // Publicar evento
            OrderPaymentRequestedEvent event = new OrderPaymentRequestedEvent(
                    orden.getId(),
                    orden.getDireccionEnviar(),
                    orden.calcularTotal(),
                    medioPago
            );

            publisher.publishOrderPaymentRequested(event);
            log.debug("Evento OrderPaymentRequestEvent publicado para orden {}", orden.getId());
        }, () -> log.error("Orden con ID {} no encontrada", idOrden));
    }

    private void guardarYEnviarAValidarOrden(Orden orden) {
        orden.enviarAValidacion();
        //Guardamos en BD
        ordenRepository.save(orden);
        // Publicar evento de orden creada para validación externa
        publisher.publishOrderOrdenCreated(this.getEvent(orden));
        log.debug("Orden {} enviada a validación y evento publicado", orden.getId());
    }

    private OrdenCreadaEvent getEvent(Orden orden){
        return new OrdenCreadaEvent(
                orden.getId(),
                orden.getClienteId(),
                orden.getDireccionEnviar(),
                orden.getItems().stream()
                        .map(i -> new OrdenCreadaEvent.ItemOrdenCreada(i.getProductoId(), i.getCantidad()))
                        .collect(Collectors.toList())
        );
    }
}
