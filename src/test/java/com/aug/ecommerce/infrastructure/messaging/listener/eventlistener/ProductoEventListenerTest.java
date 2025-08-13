package com.aug.ecommerce.infrastructure.messaging.listener.eventlistener;

import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.services.ProductoValidacionService;
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
 * Verifica que el listener delega en ProductoValidacionService.
 * Si el servicio falla, la excepción se propaga.
 */
@ExtendWith(MockitoExtension.class)
class ProductoEventListenerTest {

    @Mock
    private ProductoValidacionService productoValidacionService;

    @InjectMocks
    private ProductoEventListener listener;

    @Test
    @DisplayName("handle: invoca validarProductoCreacionOrden con orden e items")
    void handle_ok() throws Exception {
        var event = new OrdenCreadaEvent(
                1001L, 2002L, "CL 1 # 2-3",
                List.of(new OrdenCreadaEvent.ItemOrdenCreada(10L, 3),
                        new OrdenCreadaEvent.ItemOrdenCreada(20L, 1))
        );

        listener.handle(event);

        verify(productoValidacionService, times(1))
                .validarProductoCreacionOrden(1001L, event.items());
        verifyNoMoreInteractions(productoValidacionService);
    }

    @Test
    @DisplayName("handle: si el servicio lanza, se propaga la excepción")
    void handle_fallaServicio_propagado() throws Exception {
        var event = new OrdenCreadaEvent(1L, 2L, "X", List.of());
        doThrow(new RuntimeException("boom"))
                .when(productoValidacionService).validarProductoCreacionOrden(1L, event.items());

        assertThrows(RuntimeException.class, () -> listener.handle(event));
    }
}
