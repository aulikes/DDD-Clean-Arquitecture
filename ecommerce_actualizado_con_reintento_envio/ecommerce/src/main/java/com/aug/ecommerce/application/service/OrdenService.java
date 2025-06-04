package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.command.RealizarOrdenCommand;
import com.aug.ecommerce.application.command.RealizarPagoCommand;
import com.aug.ecommerce.application.event.OrdenPagadaEvent;
import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.event.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.publisher.OrdenEventPublisher;
import com.aug.ecommerce.domain.model.orden.EstadoOrden;
import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final OrdenEventPublisher publisher;

    @Transactional
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

    @Transactional
    public void marcarOrdenValidada(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + ordenId));
        orden.marcarListaParaPago();
        ordenRepository.save(orden);
        log.debug("Orden {} marcada como LISTA_PARA_PAGO", ordenId);
    }

    @Transactional
    public void marcarOrdenFallida(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + ordenId));
        if (orden.getEstado().equals(EstadoOrden.deTipo(EstadoOrden.Tipo.VALIDACION_FALLIDA))) return;
        orden.marcarValidacionFallida();
        ordenRepository.save(orden);
        log.debug("Orden {} marcada como VALIDACION_FALLIDA", ordenId);
    }

    @Transactional
    public void reenviarOrdenAValidacion(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + ordenId));
        guardarYEnviarAValidarOrden(orden);
    }

    /**
     * Solicita el inicio del proceso de pago para una orden.
     * Publica un evento al exterior con la intención de realizar el pago.
     */
    @Transactional
    public void solicitarPago(RealizarPagoCommand command){
        ordenRepository.findById(command.idOrden()).ifPresentOrElse(orden -> {
            orden.iniciarPago();
            ordenRepository.save(orden);
            log.debug("Orden {} actualizada a estado PAGO_EN_PROCESO", orden.getId());

            // Publicar evento
            OrdenPreparadaParaPagoEvent event = new OrdenPreparadaParaPagoEvent(
                    orden.getId(),
                    orden.calcularTotal(),
                    command.medioPago()
            );

            publisher.publishOrdenPagoRequerido(event);
            log.debug("Evento OrderPaymentRequestEvent publicado para orden {}", orden.getId());
        }, () -> log.error("Orden con ID {} no encontrada", command.idOrden()));
    }

    @Transactional
    public void pagoConfirmado(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + ordenId));
        orden.confirmarPago();
        orden = ordenRepository.save(orden);
        log.debug("Orden {} pasó a estado PAGADA", ordenId);
        publisher.publishOrdenEnvioRequerido(
                new OrdenPagadaEvent(orden.getId(), orden.getDireccionEnviar()));
        log.debug("Evento EnvioRequestedEvent publicado para orden {}", ordenId);
    }

    @Transactional
    public void pagoRechazado(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + ordenId));
        orden.registrarPagoFallido();
        ordenRepository.save(orden);
        log.debug("Orden {} pasó a estado PAGO_RECHAZADO", ordenId);
    }

    @Transactional
    public void envioConfirmado(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + ordenId));
        orden.iniciarEnvio();
        ordenRepository.save(orden);
        log.debug("Orden {} pasó a estado INICIANDO_ENVIO", ordenId);
    }

    @Transactional
    public void envioError(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + ordenId));
        orden.registrarPagoFallido();
        ordenRepository.save(orden);
        log.debug("Orden {} pasó a estado NO_CONFIRMADO_ENVIO", ordenId);
    }

    private void guardarYEnviarAValidarOrden(Orden orden) {
        // Guardamos en BD
        orden = ordenRepository.save(orden);
        // Envías la orden a validación
        orden.enviarAValidacion();
        // Publicar evento de orden creada para validación externa
        publisher.publishOrdenCreated(this.getEvent(orden));
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
