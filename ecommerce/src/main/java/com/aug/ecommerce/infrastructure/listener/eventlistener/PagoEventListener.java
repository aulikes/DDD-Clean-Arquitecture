package com.aug.ecommerce.infrastructure.listener.eventlistener;

import com.aug.ecommerce.application.event.OrdenAPagarEvent;
import com.aug.ecommerce.application.service.PagoService;
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
    public void handle(OrdenAPagarEvent event) {
        log.info("Recibido OrderPaymentRequestEvent para la orden: {}", event.ordenId());
        pagoService.realizarPago(event);
    }
}
