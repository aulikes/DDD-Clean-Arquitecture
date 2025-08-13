package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.events.ProductoNoValidadoEvent;
import com.aug.ecommerce.application.events.ProductoValidadoEvent;
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
class OrdenRabbitListenerProductoTest {

    @Mock ObjectMapper objectMapper;
    @Mock OrdenValidacionService ordenValidacionService;

    @InjectMocks OrdenRabbitListener listener;

    @Test
    @DisplayName("producto.valido -> registrarValidacionExitosa(CON PRODUCTO)")
    void productoValido() throws Exception {
        var ev = new ProductoValidadoEvent(2001L);
        var wrapper = IntegrationEventWrapper.wrap(ev, ev.getEventType(), ev.getVersion(), "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(ProductoValidadoEvent.class))).thenReturn(ev);

        listener.recibirDesdeProductoQueue("{json}");

        verify(ordenValidacionService).registrarValidacionExitosa(2001L, ValidacionCrearOrden.PRODUCTO);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("producto.no-valido -> registrarValidacionFallida(CON PRODUCTO)")
    void productoNoValido() throws Exception {
        var ev = new ProductoNoValidadoEvent(2002L);
        var wrapper = IntegrationEventWrapper.wrap(ev, ev.getEventType(), ev.getVersion(), "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(ProductoNoValidadoEvent.class))).thenReturn(ev);

        listener.recibirDesdeProductoQueue("{json}");

        verify(ordenValidacionService).registrarValidacionFallida(2002L, ValidacionCrearOrden.PRODUCTO);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("evento desconocido (producto) -> no invoca servicio")
    void productoEventoDesconocido() throws Exception {
        var wrapper = IntegrationEventWrapper.wrap(new Object(), "producto.evento.desconocido", "v1", "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);

        listener.recibirDesdeProductoQueue("{json}");

        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("JSON inválido (producto) -> no lanza y no invoca servicio")
    void productoJsonInvalido() throws Exception {
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class)))
                .thenThrow(new RuntimeException("mapper error"));

        assertDoesNotThrow(() -> listener.recibirDesdeProductoQueue("{json-mal}"));
        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("excepción del servicio (producto) -> capturada (no propaga)")
    void productoServicioLanza() throws Exception {
        var ev = new ProductoValidadoEvent(2003L);
        var wrapper = IntegrationEventWrapper.wrap(ev, ev.getEventType(), ev.getVersion(), "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(ProductoValidadoEvent.class))).thenReturn(ev);
        doThrow(new RuntimeException("svc")).when(ordenValidacionService)
                .registrarValidacionExitosa(2003L, ValidacionCrearOrden.PRODUCTO);

        assertDoesNotThrow(() -> listener.recibirDesdeProductoQueue("{json}"));
        verify(ordenValidacionService).registrarValidacionExitosa(2003L, ValidacionCrearOrden.PRODUCTO);
    }
}
