package com.aug.ecommerce.infrastructure.messaging.listener.eventlistener;

import com.aug.ecommerce.application.events.OrdenPagadaEvent;
import com.aug.ecommerce.application.services.EnvioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnvioEventListener {
    private final EnvioService envioService;

    @Transactional
    @EventListener
    public void manejarPagoConfirmado(OrdenPagadaEvent event) {
        envioService.crearEnvio(event.ordenId(), event.direccionEnvio());
    }
}
