package com.aug.ecommerce.infrastructure.messaging.listener.eventlistener;

import com.aug.ecommerce.application.events.EnvioPreparadoEvent;
import com.aug.ecommerce.application.services.OrdenValidacionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * El archivo mostrado contiene explícitamente onOrdenPreparada(EnvioPreparadoEvent).
 * Este método SÍ captura y relanza (throw new Exception(e)).
 */
@ExtendWith(MockitoExtension.class)
class OrdenEventListenerTest {

    @Mock OrdenValidacionService ordenValidacionService;
    @InjectMocks OrdenEventListener listener;

    @Test
    @DisplayName("onOrdenPreparada: delega en gestionarInformacionEnvio")
    void onOrdenPreparada_ok() throws Exception {
        var event = new EnvioPreparadoEvent(1001L, 5001L, Instant.now(), true, "TRK-1", null);

        listener.onOrdenPreparada(event);

        verify(ordenValidacionService, times(1)).gestionarInformacionEnvio(event);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    @DisplayName("onOrdenPreparada: si el servicio falla, el listener relanza Exception")
    void onOrdenPreparada_fallaServicio_relanza() throws Exception {
        var event = new EnvioPreparadoEvent(1L, 2L, Instant.now(), false, null, "err");
        doThrow(new RuntimeException("boom"))
                .when(ordenValidacionService).gestionarInformacionEnvio(event);

        assertThrows(Exception.class, () -> listener.onOrdenPreparada(event));
    }
}
