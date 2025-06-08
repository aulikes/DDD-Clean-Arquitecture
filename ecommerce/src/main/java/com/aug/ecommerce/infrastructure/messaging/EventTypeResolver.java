package com.aug.ecommerce.infrastructure.messaging;

import com.aug.ecommerce.application.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventTypeResolver {

    private static final Map<Class<? extends IntegrationEvent>, String> EVENT_TYPES = new HashMap<>();

    static {
        EVENT_TYPES.put(OrdenCreadaEvent.class, "orden.multicast.creada");
        EVENT_TYPES.put(OrdenPreparadaParaPagoEvent.class, "orden.pago.solicitar");

        EVENT_TYPES.put(ClienteValidadoEvent.class, "cliente.orden.valido");
        EVENT_TYPES.put(ClienteNoValidadoEvent.class, "cliente.orden.no-valido");

        EVENT_TYPES.put(ProductoCreadoEvent.class, "producto.inventario.crear");
        EVENT_TYPES.put(ProductoValidadoEvent.class, "producto.orden.valido");
        EVENT_TYPES.put(ProductoNoValidadoEvent.class, "producto.orden.no-valido");

        EVENT_TYPES.put(InventarioValidadoEvent.class, "inventario.orden.disponible");
        EVENT_TYPES.put(InventarioNoValidadoEvent.class, "inventario.orden.no-disponible");

        EVENT_TYPES.put(PagoConfirmadoEvent.class, "pago.orden.confirmado");

        EVENT_TYPES.put(EnvioPreparadoEvent.class, "envio.orden.preparado");
        EVENT_TYPES.put(OrdenPagadaEvent.class, "orden.envio.preparar");
    }

    public String resolveEventType(IntegrationEvent event) {
        return EVENT_TYPES.getOrDefault(event.getClass(), "evento.desconocido");
    }
}
