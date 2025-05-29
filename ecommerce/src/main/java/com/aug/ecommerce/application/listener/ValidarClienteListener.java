package com.aug.ecommerce.application.listener;

import com.aug.ecommerce.application.event.ClienteNoValidoEvent;
import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.event.ClienteValidoEvent;
import com.aug.ecommerce.application.publisher.ClienteEventPublisher;
import com.aug.ecommerce.domain.model.cliente.Cliente;
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
    public void handle(OrdenCreadaEvent event) throws Exception {
        log.debug("---> Entrando al Listener ValidarClienteListener - OrdenCreadaEvent {}", event.getOrdenId());
        Long clienteId = event.getClienteId();
        try {
           clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + clienteId));
           log.debug("Cliente {} válido para orden {}", clienteId, event.getOrdenId());
           publisher.publishClienteValido(new ClienteValidoEvent(event.getOrdenId()));
        } catch (Exception e) {
            log.error("Cliente {} NO válido para orden {}", clienteId, event.getOrdenId());
            publisher.publishClienteNoValido(new ClienteNoValidoEvent(event.getOrdenId()));
            throw new Exception(e);
        }
    }
}
