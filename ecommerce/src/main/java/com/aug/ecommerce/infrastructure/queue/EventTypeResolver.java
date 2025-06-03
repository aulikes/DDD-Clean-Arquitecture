package com.aug.ecommerce.infrastructure.queue;

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
        EVENT_TYPES.put(OrdenAPagarEvent.class, "orden.pago.solicitar");

        EVENT_TYPES.put(ClienteValidoEvent.class, "cliente.orden.valido");
        EVENT_TYPES.put(ClienteNoValidoEvent.class, "cliente.orden.no-valido");

        EVENT_TYPES.put(ProductoCreadoEvent.class, "producto.inventario.crear");
        EVENT_TYPES.put(ProductoValidoEvent.class, "producto.orden.valido");
        EVENT_TYPES.put(ProductoNoValidoEvent.class, "producto.orden.no-valido");

        EVENT_TYPES.put(InventarioDisponibleEvent.class, "inventario.orden.disponible");
        EVENT_TYPES.put(InventarioNoDisponibleEvent.class, "inventario.orden.no-disponible");

        EVENT_TYPES.put(PagoConfirmadoEvent.class, "pago.orden.confirmado");

        EVENT_TYPES.put(EnvioPreparadoEvent.class, "envio.orden.preparado");
        EVENT_TYPES.put(EnvioRequestedEvent.class, "orden.envio.preparar");
    }

    public String resolveEventType(IntegrationEvent event) {
        return EVENT_TYPES.getOrDefault(event.getClass(), "evento.desconocido");
    }
}
