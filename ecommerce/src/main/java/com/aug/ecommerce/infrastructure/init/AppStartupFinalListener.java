package com.aug.ecommerce.infrastructure.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class AppStartupFinalListener {

    private final CategoriaInitializer categoriaInitializer;
    private final ClienteInitializer clienteInitializer;
    private final ProductoInitializer productoInitializer;
    private final OrdenInitializer ordenInitializer;
    private final PagoInitializer pagoInitializer;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // Aquí ya puedes estar seguro de que todo está disponible
        log.error("#################################################La aplicación está completamente lista.");

        categoriaInitializer.run();
        clienteInitializer.run();
        productoInitializer.run();
        ordenInitializer.run();
        pagoInitializer.run();
    }
}
