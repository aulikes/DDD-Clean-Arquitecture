package com.aug.ecommerce.infrastructure.messaging.listener.eventlistener;

import com.aug.ecommerce.application.commands.CrearInventarioCommand;
import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.events.ProductoCreadoEvent;
import com.aug.ecommerce.application.services.InventarioService;
import com.aug.ecommerce.application.services.InventarioValidacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventarioEventListener {

    private final InventarioService inventarioService;
    private final InventarioValidacionService inventarioValidacionService;

    @Transactional
    @EventListener
    public void crearProductoInventario(ProductoCreadoEvent event) {
        inventarioService.crearInvenario(new CrearInventarioCommand(event.productoId(), event.cantidad()));
    }

    @Transactional
    @EventListener
    public void validarInventario(OrdenCreadaEvent event) throws Exception {
        log.debug("---> Entrando al Listener ValidarInventarioListener - OrdenCreadaEvent {}", event.ordenId());
        inventarioValidacionService.validarInventarioCreacionOrden(event.ordenId(), event.items());
    }
}
