package com.aug.ecommerce.infrastructure.messaging.listener.eventlistener;

import com.aug.ecommerce.application.events.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.services.PagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PagoEventListener {
    private final PagoService pagoService;

    /**
     * Lógica principal que maneja la intención de pago desde el evento.
     * Llama al servicio de pago para registrar y procesar el pago.
     */
    @EventListener
    public void handle(OrdenPreparadaParaPagoEvent event) {
        log.info("Recibido OrderPaymentRequestEvent para la orden: {}", event.ordenId());
        pagoService.realizarPago(event);
    }
}
