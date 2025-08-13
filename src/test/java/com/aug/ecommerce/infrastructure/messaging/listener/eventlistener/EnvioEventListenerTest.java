package com.aug.ecommerce.infrastructure.messaging.listener.eventlistener;

import com.aug.ecommerce.application.events.OrdenPagadaEvent;
import com.aug.ecommerce.application.services.EnvioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Verifica que manejarPagoConfirmado llama a EnvioService.crearEnvio.
 * Si el servicio falla, la excepción se propaga (no hay try/catch).
 */
@ExtendWith(MockitoExtension.class)
class EnvioEventListenerTest {

    @Mock
    private EnvioService envioService;

    @InjectMocks
    private EnvioEventListener listener;

    @Test
    @DisplayName("manejarPagoConfirmado: crea envío con ordenId y dirección")
    void manejarPagoConfirmado_ok() {
        var event = new OrdenPagadaEvent(1001L, "CL 1 # 2-3");

        listener.manejarPagoConfirmado(event);

        verify(envioService, times(1)).crearEnvio(1001L, "CL 1 # 2-3");
        verifyNoMoreInteractions(envioService);
    }

    @Test
    @DisplayName("manejarPagoConfirmado: si el servicio lanza, se propaga la excepción")
    void manejarPagoConfirmado_fallaServicio_propagado() {
        var event = new OrdenPagadaEvent(1L, "X");
        doThrow(new RuntimeException("boom"))
                .when(envioService).crearEnvio(1L, "X");

        assertThrows(RuntimeException.class, () -> listener.manejarPagoConfirmado(event));
    }
}
