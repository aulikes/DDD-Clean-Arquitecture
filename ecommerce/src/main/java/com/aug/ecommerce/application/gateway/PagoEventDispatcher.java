package com.aug.ecommerce.application.gateway;

public interface PagoEventDispatcher {
    void publicarPagoRealizado(Object Evento);
}
