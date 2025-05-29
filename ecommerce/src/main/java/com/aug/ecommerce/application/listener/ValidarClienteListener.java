package com.aug.ecommerce.application.listener;

import com.aug.ecommerce.application.event.ClienteNoValidoEvent;
import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.event.ClienteValidoEvent;
import com.aug.ecommerce.application.publisher.ClienteEventPublisher;
import com.aug.ecommerce.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidarClienteListener {

    private final ClienteRepository clienteRepository;
    private final ClienteEventPublisher publisher;

    @EventListener
    public void handle(OrdenCreadaEvent event) {
        Long clienteId = event.getClienteId();
        clienteRepository.findById(clienteId).ifPresentOrElse(cliente -> {
            log.debug("Cliente {} válido para orden {}", clienteId, event.getOrdenId());
            publisher.publishClienteValido(new ClienteValidoEvent(event.getOrdenId()));
        }, () -> {
            log.warn("Cliente {} NO válido para orden {}", clienteId, event.getOrdenId());
            publisher.publishClienteNoValido(new ClienteNoValidoEvent(event.getOrdenId()));
        });
    }
}
