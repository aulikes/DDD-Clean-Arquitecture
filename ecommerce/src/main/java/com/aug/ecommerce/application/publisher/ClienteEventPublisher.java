package com.aug.ecommerce.application.publisher;

import com.aug.ecommerce.application.event.ClienteNoValidoEvent;
import com.aug.ecommerce.application.event.ClienteValidoEvent;

public interface ClienteEventPublisher {
    void publishClienteValido(ClienteValidoEvent event);
    void publishClienteNoValido(ClienteNoValidoEvent event);
}
