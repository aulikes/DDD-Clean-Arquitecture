package com.aug.ecommerce.application.listener;

import com.aug.ecommerce.application.command.CrearInventarioCommand;
import com.aug.ecommerce.application.event.PagoConfirmadoEvent;
import com.aug.ecommerce.application.event.ProductoCreadoEvent;
import com.aug.ecommerce.application.service.InventarioService;
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

    @Transactional
    @EventListener
    public void manejarPagoConfirmado(ProductoCreadoEvent event) {
        inventarioService.crearInvenario(new CrearInventarioCommand(event.productoId(), event.cantidad()));
    }
}
