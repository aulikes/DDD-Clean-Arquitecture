package com.aug.ecommerce.application.publishers;

import com.aug.ecommerce.application.events.IntegrationEvent;

public interface PagoEventPublisher {
    void publicarPagoRealizado(IntegrationEvent evento);
}
