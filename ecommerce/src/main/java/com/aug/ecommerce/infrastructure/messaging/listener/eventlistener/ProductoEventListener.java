package com.aug.ecommerce.infrastructure.messaging.listener.eventlistener;

import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.services.ProductoValidacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductoEventListener {

    private final ProductoValidacionService productoValidacionService;

    @EventListener
    public void handle(OrdenCreadaEvent event) throws Exception {
        log.debug("---> Entrando al Listener ValidarProductoListener - OrdenCreadaEvent {}", event.ordenId());
        productoValidacionService.validarProductoCreacionOrden(event.ordenId(), event.items());
    }
}
