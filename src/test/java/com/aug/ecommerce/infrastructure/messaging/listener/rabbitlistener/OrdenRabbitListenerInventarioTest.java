package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.events.InventarioNoValidadoEvent;
import com.aug.ecommerce.application.events.InventarioValidadoEvent;
import com.aug.ecommerce.application.services.OrdenValidacionService;
import com.aug.ecommerce.application.services.ValidacionCrearOrden;
import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenRabbitListenerInventarioTest {

    @Mock ObjectMapper objectMapper;
    @Mock OrdenValidacionService ordenValidacionService;

    @InjectMocks OrdenRabbitListener listener;

    @Test
    @DisplayName("inventario.disponible -> registrarValidacionExitosa(CON STOCK)")
    void inventarioDisponible() throws Exception {
        var ev = new InventarioValidadoEvent(3001L);
        var wrapper = IntegrationEventWrapper.wrap(ev, ev.getEventType(), ev.getVersion(), "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(InventarioValidadoEvent.class))).thenReturn(ev);

        listener.recibirDesdeInventarioQueue("{json}");

        verify(ordenValidacionService).registrarValidacionExitosa(3001L, ValidacionCrearOrden.STOCK);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("inventario.no-disponible -> registrarValidacionFallida(CON STOCK)")
    void inventarioNoDisponible() throws Exception {
        var ev = new InventarioNoValidadoEvent(3002L);
        var wrapper = IntegrationEventWrapper.wrap(ev, ev.getEventType(), ev.getVersion(), "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(InventarioNoValidadoEvent.class))).thenReturn(ev);

        listener.recibirDesdeInventarioQueue("{json}");

        verify(ordenValidacionService).registrarValidacionFallida(3002L, ValidacionCrearOrden.STOCK);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("evento desconocido (inventario) -> no invoca servicio")
    void inventarioEventoDesconocido() throws Exception {
        var wrapper = IntegrationEventWrapper.wrap(new Object(), "inventario.evento.desconocido", "v1", "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);

        listener.recibirDesdeInventarioQueue("{json}");

        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("JSON inválido (inventario) -> no lanza y no invoca servicio")
    void inventarioJsonInvalido() throws Exception {
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class)))
                .thenThrow(new RuntimeException("mapper error"));

        assertDoesNotThrow(() -> listener.recibirDesdeInventarioQueue("{json-mal}"));
        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("excepción del servicio (inventario) -> capturada (no propaga)")
    void inventarioServicioLanza() throws Exception {
        var ev = new InventarioValidadoEvent(3003L);
        var wrapper = IntegrationEventWrapper.wrap(ev, ev.getEventType(), ev.getVersion(), "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(InventarioValidadoEvent.class))).thenReturn(ev);
        doThrow(new RuntimeException("svc")).when(ordenValidacionService)
                .registrarValidacionExitosa(3003L, ValidacionCrearOrden.STOCK);

        assertDoesNotThrow(() -> listener.recibirDesdeInventarioQueue("{json}"));
        verify(ordenValidacionService).registrarValidacionExitosa(3003L, ValidacionCrearOrden.STOCK);
    }
}
