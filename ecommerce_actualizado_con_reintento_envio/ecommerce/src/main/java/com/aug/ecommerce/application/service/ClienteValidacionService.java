package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.event.ClienteNoValidadoEvent;
import com.aug.ecommerce.application.event.ClienteValidadoEvent;
import com.aug.ecommerce.application.publisher.ClienteEventPublisher;
import com.aug.ecommerce.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación encargado de coordinar la validación de clientes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteValidacionService {

    private final ClienteRepository clienteRepository;
    private final ClienteEventPublisher publisher;

    public void validarClienteCreacionOrden(Long ordenId, Long clienteId) throws Exception {
        try {
            clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + clienteId));
            log.debug("Cliente {} válido para orden {}", clienteId, ordenId);
            publisher.publishClienteValido(new ClienteValidadoEvent(ordenId));
        } catch (Exception e) {
            log.error("Cliente {} NO válido para orden {}", clienteId, ordenId);
            publisher.publishClienteNoValido(new ClienteNoValidadoEvent(ordenId));
//            throw new Exception(e);
        }

    }
}
