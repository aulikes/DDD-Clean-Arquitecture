package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.events.ClienteNoValidadoEvent;
import com.aug.ecommerce.application.events.ClienteValidadoEvent;
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
class OrdenRabbitListenerClienteTest {

    @Mock ObjectMapper objectMapper;
    @Mock OrdenValidacionService ordenValidacionService;

    @InjectMocks OrdenRabbitListener listener;

    @Test
    @DisplayName("cliente.valido -> registrarValidacionExitosa(CON CLIENTE)")
    void clienteValido() throws Exception {
        var ev = new ClienteValidadoEvent(1001L);
        var wrapper = IntegrationEventWrapper.wrap(ev, ev.getEventType(), ev.getVersion(), "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(ClienteValidadoEvent.class))).thenReturn(ev);

        listener.recibirDesdeClienteQueue("{json}");

        verify(ordenValidacionService).registrarValidacionExitosa(1001L, ValidacionCrearOrden.CLIENTE);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("cliente.no-valido -> registrarValidacionFallida(CON CLIENTE)")
    void clienteNoValido() throws Exception {
        var ev = new ClienteNoValidadoEvent(1002L);
        var wrapper = IntegrationEventWrapper.wrap(ev, ev.getEventType(), ev.getVersion(), "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(ClienteNoValidadoEvent.class))).thenReturn(ev);

        listener.recibirDesdeClienteQueue("{json}");

        verify(ordenValidacionService).registrarValidacionFallida(1002L, ValidacionCrearOrden.CLIENTE);
        verifyNoMoreInteractions(ordenValidacionService);
    }
    @Test
    @DisplayName("JSON inválido (cliente) -> no lanza y no invoca servicio")
    void clienteJsonInvalido() throws Exception {
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class)))
                .thenThrow(new RuntimeException("mapper error"));

        assertDoesNotThrow(() -> listener.recibirDesdeClienteQueue("{json-mal}"));
        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("excepción del servicio (cliente) -> capturada (no propaga)")
    void clienteServicioLanza() throws Exception {
        var ev = new ClienteValidadoEvent(1003L);
        var wrapper = IntegrationEventWrapper.wrap(ev, ev.getEventType(), ev.getVersion(), "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(ClienteValidadoEvent.class))).thenReturn(ev);
        doThrow(new RuntimeException("svc")).when(ordenValidacionService)
                .registrarValidacionExitosa(1003L, ValidacionCrearOrden.CLIENTE);

        assertDoesNotThrow(() -> listener.recibirDesdeClienteQueue("{json}"));
        verify(ordenValidacionService).registrarValidacionExitosa(1003L, ValidacionCrearOrden.CLIENTE);
    }
}
