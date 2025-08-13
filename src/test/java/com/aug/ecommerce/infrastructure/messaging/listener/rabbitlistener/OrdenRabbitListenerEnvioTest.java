package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.events.EnvioPreparadoEvent;
import com.aug.ecommerce.application.services.OrdenValidacionService;
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
class OrdenRabbitListenerEnvioTest {

    @Mock ObjectMapper objectMapper;
    @Mock OrdenValidacionService ordenValidacionService;

    @InjectMocks OrdenRabbitListener listener;

    @Test
    @DisplayName("envio.preparado -> gestionarInformacionEnvio(event)")
    void envioPreparado() throws Exception {
        var ev = new EnvioPreparadoEvent(5001L, 6001L, Instant.now(), true, "TRK-1", null);
        var wrapper = IntegrationEventWrapper.wrap(ev, ev.getEventType(), ev.getVersion(), "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(EnvioPreparadoEvent.class))).thenReturn(ev);

        listener.recibirDesdeEnvioQueue("{json}");

        verify(ordenValidacionService).gestionarInformacionEnvio(ev);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("evento desconocido (envío) -> no invoca servicio")
    void envioEventoDesconocido() throws Exception {
        var wrapper = IntegrationEventWrapper.wrap(new Object(), "envio.evento.desconocido", "v1", "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);

        listener.recibirDesdeEnvioQueue("{json}");

        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("JSON inválido (envío) -> no lanza y no invoca servicio")
    void envioJsonInvalido() throws Exception {
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class)))
                .thenThrow(new RuntimeException("mapper error"));

        assertDoesNotThrow(() -> listener.recibirDesdeEnvioQueue("{json-mal}"));
        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("excepción del servicio (envío) -> capturada (no propaga)")
    void envioServicioLanza() throws Exception {
        var ev = new EnvioPreparadoEvent(5002L, 6002L, Instant.now(), true, "TRK-2", null);
        var wrapper = IntegrationEventWrapper.wrap(ev, ev.getEventType(), ev.getVersion(), "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(EnvioPreparadoEvent.class))).thenReturn(ev);
        doThrow(new RuntimeException("svc")).when(ordenValidacionService).gestionarInformacionEnvio(ev);

        assertDoesNotThrow(() -> listener.recibirDesdeEnvioQueue("{json}"));
        verify(ordenValidacionService).gestionarInformacionEnvio(ev);
    }
}
