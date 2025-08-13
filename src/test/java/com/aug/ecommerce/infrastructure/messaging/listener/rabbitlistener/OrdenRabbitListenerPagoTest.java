package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.events.PagoConfirmadoEvent;
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
class OrdenRabbitListenerPagoTest {

    @Mock ObjectMapper objectMapper;
    @Mock OrdenValidacionService ordenValidacionService;

    @InjectMocks OrdenRabbitListener listener;

    @Test
    @DisplayName("pago.confirmado -> gestionarInformacionPago(event)")
    void pagoConfirmado() throws Exception {
        var ev = new PagoConfirmadoEvent(4001L, 9001L, Instant.now(), true, "TRX-1", null);
        var wrapper = IntegrationEventWrapper.wrap(ev, ev.getEventType(), ev.getVersion(), "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(PagoConfirmadoEvent.class))).thenReturn(ev);

        listener.recibirDesdePagoQueue("{json}");

        verify(ordenValidacionService).gestionarInformacionPago(ev);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("evento desconocido (pago) -> no invoca servicio")
    void pagoEventoDesconocido() throws Exception {
        var wrapper = IntegrationEventWrapper.wrap(new Object(), "pago.evento.desconocido", "v1", "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);

        listener.recibirDesdePagoQueue("{json}");

        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("JSON inválido (pago) -> no lanza y no invoca servicio")
    void pagoJsonInvalido() throws Exception {
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class)))
                .thenThrow(new RuntimeException("mapper error"));

        assertDoesNotThrow(() -> listener.recibirDesdePagoQueue("{json-mal}"));
        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("excepción del servicio (pago) -> capturada (no propaga)")
    void pagoServicioLanza() throws Exception {
        var ev = new PagoConfirmadoEvent(4002L, 9002L, Instant.now(), true, "TRX-2", null);
        var wrapper = IntegrationEventWrapper.wrap(ev, ev.getEventType(), ev.getVersion(), "t", Instant.now());
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(PagoConfirmadoEvent.class))).thenReturn(ev);
        doThrow(new RuntimeException("svc")).when(ordenValidacionService).gestionarInformacionPago(ev);

        assertDoesNotThrow(() -> listener.recibirDesdePagoQueue("{json}"));
        verify(ordenValidacionService).gestionarInformacionPago(ev);
    }
}
