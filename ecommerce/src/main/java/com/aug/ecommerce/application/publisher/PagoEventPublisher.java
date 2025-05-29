package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.PagoConfirmadoEvent;

public interface PagoEventPublisher {
    void publicarPagoRealizado(PagoConfirmadoEvent evento);
}
