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
    public void onClienteValido(ClienteValidoEvent event) throws Exception {
        try {
            log.debug("------------> ClienteValidoEvent");
            ordenValidacionService.registrarValidacionExitosa(event.ordenId(), "CLIENTE");
        } catch (Exception e) {
            log.error("------------> onClienteValido", e);
            throw new Exception(e);
        }
    }

    @EventListener
    public void onProductoValido(ProductoValidoEvent event) throws Exception{
        try {
            log.debug("------------> ProductoValidoEvent");
            ordenValidacionService.registrarValidacionExitosa(event.ordenId(), "PRODUCTO");
        } catch (Exception e) {
            log.error("------------> onProductoValido", e);
            throw new Exception(e);
        }
    }

    @EventListener
    public void onStockReservado(StockDisponibleEvent event) throws Exception{
        try {
            log.debug("------------> StockDisponibleEvent");
            ordenValidacionService.registrarValidacionExitosa(event.ordenId(), "STOCK");
        } catch (Exception e) {
            log.error("------------> onStockReservado", e);
            throw new Exception(e);
        }
    }

    @EventListener
    public void onClienteNoValido(ClienteNoValidoEvent event) throws Exception{
        try {
            log.debug("------------> ClienteNoValidoEvent");
            ordenValidacionService.registrarValidacionFallida(event.ordenId(), "CLIENTE");
        } catch (Exception e) {
            log.error("------------> onClienteNoValido", e);
            throw new Exception(e);
        }
    }

    @EventListener
    public void onProductoNoValido(ProductoNoValidoEvent event) throws Exception{
        try {
            log.debug("------------> ProductoNoValidoEvent");
            ordenValidacionService.registrarValidacionFallida(event.ordenId(), "PRODUCTO");
        } catch (Exception e) {
            log.error("------------> onProductoNoValido", e);
            throw new Exception(e);
        }
    }

    @EventListener
    public void onStockNoDisponible(StockNoDisponibleEvent event) throws Exception{
        try {
            log.debug("------------> StockNoDisponibleEvent");
            ordenValidacionService.registrarValidacionFallida(event.ordenId(), "STOCK");
        } catch (Exception e) {
            log.error("------------> onStockNoDisponible", e);
            throw new Exception(e);
        }
    }
}
