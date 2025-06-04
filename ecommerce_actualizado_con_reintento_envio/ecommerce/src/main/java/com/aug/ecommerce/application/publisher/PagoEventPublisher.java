package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.IntegrationEvent;

public interface PagoEventPublisher {
    void publicarPagoRealizado(IntegrationEvent evento);
}
