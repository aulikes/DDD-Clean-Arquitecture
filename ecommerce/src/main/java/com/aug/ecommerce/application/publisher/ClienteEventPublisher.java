package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.IntegrationEvent;

public interface ClienteEventPublisher {
    void publishClienteValido(IntegrationEvent event);
    void publishClienteNoValido(IntegrationEvent event);
}
