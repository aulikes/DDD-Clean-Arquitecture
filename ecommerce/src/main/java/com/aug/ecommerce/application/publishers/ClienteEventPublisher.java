package com.aug.ecommerce.application.publishers;

import com.aug.ecommerce.application.events.IntegrationEvent;

public interface ClienteEventPublisher {
    void publishClienteValido(IntegrationEvent event);
    void publishClienteNoValido(IntegrationEvent event);
}
