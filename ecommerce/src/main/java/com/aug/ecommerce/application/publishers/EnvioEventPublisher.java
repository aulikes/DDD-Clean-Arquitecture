package com.aug.ecommerce.application.publishers;

import com.aug.ecommerce.application.events.IntegrationEvent;

public interface EnvioEventPublisher {
    void publicarEnvioPreparado(IntegrationEvent evento);
}
