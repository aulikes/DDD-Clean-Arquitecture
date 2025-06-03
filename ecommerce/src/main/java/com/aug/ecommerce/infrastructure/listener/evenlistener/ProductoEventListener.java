package com.aug.ecommerce.infrastructure.listener.evenlistener;

import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.service.ProductoValidacionService;
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
        log.debug("---> Entrando al Listener ValidarProductoListener - OrdenCreadaEvent {}", event.getOrdenId());
        productoValidacionService.validarProductoCreacionOrden(event.getOrdenId(), event.getItems());
    }
}
