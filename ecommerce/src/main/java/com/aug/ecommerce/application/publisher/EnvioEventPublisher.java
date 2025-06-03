package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.IntegrationEvent;

public interface EnvioEventPublisher {
    void publicarEnvioPreparado(IntegrationEvent evento);
}
