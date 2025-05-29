package com.aug.ecommerce.application.listener;

import com.aug.ecommerce.application.event.*;
import com.aug.ecommerce.application.service.OrdenValidacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrdenEventListener {

    private final OrdenValidacionService ordenValidacionService;

    @EventListener
    public void onClienteValido(ClienteValidoEvent event) {
        ordenValidacionService.registrarValidacionExitosa(event.ordenId(), "CLIENTE");
    }

    @EventListener
    public void onProductoValido(ProductoValidoEvent event) {
        ordenValidacionService.registrarValidacionExitosa(event.ordenId(), "PRODUCTO");
    }

    @EventListener
    public void onStockReservado(StockNoDisponibleEvent event) {
        ordenValidacionService.registrarValidacionExitosa(event.ordenId(), "STOCK");
    }

    @EventListener
    public void onClienteInvalido(ClienteNoValidoEvent event) {
        ordenValidacionService.registrarValidacionFallida(event.ordenId(), "CLIENTE");
    }

    @EventListener
    public void onProductoInvalido(ProductoNoValidoEvent event) {
        ordenValidacionService.registrarValidacionFallida(event.ordenId(), "PRODUCTO");
    }

    @EventListener
    public void onStockNoDisponible(StockNoDisponibleEvent event) {
        ordenValidacionService.registrarValidacionFallida(event.ordenId(), "STOCK");
    }
}
