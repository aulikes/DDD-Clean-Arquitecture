package com.aug.ecommerce.infrastructure.messaging.listener.eventlistener;

import com.aug.ecommerce.application.events.*;
import com.aug.ecommerce.application.services.OrdenValidacionService;
import com.aug.ecommerce.application.services.ValidacionCrearOrden;
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
    public void onClienteValido(ClienteValidadoEvent event) throws Exception {
        try {
            log.debug("------------> ClienteValidoEvent");
            ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.CLIENTE);
        } catch (Exception e) {
            log.error("------------> onClienteValido", e);
            throw new Exception(e);
        }
    }

    @EventListener
    public void onProductoValido(ProductoValidadoEvent event) throws Exception{
        try {
            log.debug("------------> ProductoValidoEvent");
            ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.PRODUCTO);
        } catch (Exception e) {
            log.error("------------> onProductoValido", e);
            throw new Exception(e);
        }
    }

    @EventListener
    public void onStockDisponible(InventarioValidadoEvent event) throws Exception{
        try {
            log.debug("------------> StockDisponibleEvent");
            ordenValidacionService.registrarValidacionExitosa(event.ordenId(), ValidacionCrearOrden.STOCK);
        } catch (Exception e) {
            log.error("------------> onStockReservado", e);
            throw new Exception(e);
        }
    }

    @EventListener
    public void onStockNoDisponible(InventarioNoValidadoEvent event) throws Exception{
        try {
            log.debug("------------> StockNoDisponibleEvent");
            ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.STOCK);
        } catch (Exception e) {
            log.error("------------> onStockReservado", e);
            throw new Exception(e);
        }
    }

    @EventListener
    public void onClienteNoValido(ClienteNoValidadoEvent event) throws Exception{
        try {
            log.debug("------------> ClienteNoValidoEvent");
            ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.CLIENTE);
        } catch (Exception e) {
            log.error("------------> onClienteNoValido", e);
            throw new Exception(e);
        }
    }

    @EventListener
    public void onProductoNoValido(ProductoNoValidadoEvent event) throws Exception{
        try {
            log.debug("------------> ProductoNoValidoEvent");
            ordenValidacionService.registrarValidacionFallida(event.ordenId(), ValidacionCrearOrden.PRODUCTO);
        } catch (Exception e) {
            log.error("------------> onProductoNoValido", e);
            throw new Exception(e);
        }
    }

    @EventListener
    public void onOrdenPagada(PagoConfirmadoEvent event) throws Exception{
        try {
            log.debug("------------> PagoConfirmadoEvent");
            ordenValidacionService.gestionarInformacionPago(event);
        } catch (Exception e) {
            log.error("------------> PagoConfirmadoEvent", e);
            throw new Exception(e);
        }
    }

    @EventListener
    public void onOrdenPreparada(EnvioPreparadoEvent event) throws Exception{
        try {
            log.debug("------------> EnvioPreparadoEvent");
            ordenValidacionService.gestionarInformacionEnvio(event);
        } catch (Exception e) {
            log.error("------------> EnvioPreparadoEvent", e);
            throw new Exception(e);
        }
    }

}
