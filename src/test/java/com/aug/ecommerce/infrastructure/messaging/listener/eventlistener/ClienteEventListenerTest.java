package com.aug.ecommerce.infrastructure.messaging.listener.eventlistener;

import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.services.ClienteValidacionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Verifica que el listener delega en ClienteValidacionService.
 * Esta clase NO captura excepciones -> si el servicio falla, se propaga.
 */
@ExtendWith(MockitoExtension.class)
class ClienteEventListenerTest {

    @Mock
    private ClienteValidacionService clienteValidacionService;

    @InjectMocks
    private ClienteEventListener listener;

    @Test
    @DisplayName("validarCliente: invoca validarClienteCreacionOrden con IDs correctos")
    void validarCliente_ok() throws Exception {
        var event = new OrdenCreadaEvent(
                1001L, 2002L, "CL 1 # 2-3",
                List.of(new OrdenCreadaEvent.ItemOrdenCreada(10L, 2))
        );

        listener.validarCliente(event);

        verify(clienteValidacionService, times(1))
                .validarClienteCreacionOrden(1001L, 2002L);
        verifyNoMoreInteractions(clienteValidacionService);
    }

    @Test
    @DisplayName("validarCliente: si el servicio lanza, se propaga la excepción")
    void validarCliente_fallaServicio_propagado() throws Exception {
        var event = new OrdenCreadaEvent(1L, 2L, "X", List.of());
        doThrow(new RuntimeException("boom"))
                .when(clienteValidacionService).validarClienteCreacionOrden(1L, 2L);

        assertThrows(RuntimeException.class, () -> listener.validarCliente(event));
    }
}
