package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.PagoConfirmadoEvent;

public interface PagoEventDispatcher {
    void publicarPagoRealizado(PagoConfirmadoEvent evento);
}
