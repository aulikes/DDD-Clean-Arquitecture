package com.aug.ecommerce.application.listener;

import com.aug.ecommerce.application.event.ClienteNoValidoEvent;
import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.event.ProductoNoValidoEvent;
import com.aug.ecommerce.application.event.ProductoValidoEvent;
import com.aug.ecommerce.application.publisher.ProductoEventPublisher;
import com.aug.ecommerce.domain.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidarProductoListener {

    private final ProductoRepository productoRepository;
    private final ProductoEventPublisher publisher;

    @EventListener
    public void handle(OrdenCreadaEvent event) throws Exception {
        log.debug("---> Entrando al Listener ValidarProductoListener - OrdenCreadaEvent {}", event.getOrdenId());
        try {
            boolean todosExisten = event.getItems().stream()
                    .allMatch(item -> productoRepository.findById(item.getProductoId()).isPresent());

            if (todosExisten) {
                log.debug("Todos los productos válidos para orden {}", event.getOrdenId());
                publisher.publishProductoValido(new ProductoValidoEvent(event.getOrdenId()));
            } else {
                throw new RuntimeException("Productos NO válidos para orden " + event.getOrdenId());
            }
        } catch (Exception e) {
            log.error("Productos No válidos para orden {}", event.getOrdenId());
            publisher.publishProductoInvalido(new ProductoNoValidoEvent(event.getOrdenId()));
            throw new Exception(e);
        }
    }
}
