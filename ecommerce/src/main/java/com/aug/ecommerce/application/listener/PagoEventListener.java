package com.aug.ecommerce.application.listener;

import com.aug.ecommerce.application.event.OrderPaymentRequestedEvent;
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
    public void handle(OrderPaymentRequestedEvent event) {
        log.info("Recibido OrderPaymentRequestEvent para la orden: {}", event.ordenId());
        pagoService.realizarPago(event);
    }
}
