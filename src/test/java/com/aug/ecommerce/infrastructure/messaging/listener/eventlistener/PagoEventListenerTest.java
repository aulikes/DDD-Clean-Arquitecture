package com.aug.ecommerce.infrastructure.messaging.listener.eventlistener;

import com.aug.ecommerce.application.events.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.services.PagoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Verifica que handle delega en PagoService.realizarPago.
 * No hay manejo de excepciones -> se propagan.
 */
@ExtendWith(MockitoExtension.class)
class PagoEventListenerTest {

    @Mock PagoService pagoService;
    @InjectMocks PagoEventListener listener;

    @Test
    @DisplayName("handle: invoca pagoService.realizarPago con el evento")
    void handle_ok() {
        var event = new OrdenPreparadaParaPagoEvent(1001L, 123.45, "TARJETA");
        listener.handle(event);
        verify(pagoService, times(1)).realizarPago(event);
        verifyNoMoreInteractions(pagoService);
    }

    @Test
    @DisplayName("handle: si el servicio lanza, la excepción se propaga")
    void handle_fallaServicio_propagado() {
        var event = new OrdenPreparadaParaPagoEvent(1L, 10.0, "X");
        doThrow(new RuntimeException("boom")).when(pagoService).realizarPago(event);
        assertThrows(RuntimeException.class, () -> listener.handle(event));
    }
}
