package com.aug.ecommerce.infrastructure.messaging.listener.eventlistener;

import com.aug.ecommerce.application.commands.CrearInventarioCommand;
import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.events.ProductoCreadoEvent;
import com.aug.ecommerce.application.services.InventarioService;
import com.aug.ecommerce.application.services.InventarioValidacionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Cubre ambos métodos visibles en el archivo:
 * - crearProductoInventario(ProductoCreadoEvent)
 * - validarInventario(OrdenCreadaEvent)
 * Si el servicio falla, la excepción se propaga (no hay try/catch).
 */
@ExtendWith(MockitoExtension.class)
class InventarioEventListenerTest {

    @Mock InventarioService inventarioService;
    @Mock InventarioValidacionService inventarioValidacionService;

    @InjectMocks InventarioEventListener listener;

    @Test
    @DisplayName("crearProductoInventario: construye y envía CrearInventarioCommand correcto")
    void crearProductoInventario_ok() {
        var event = new ProductoCreadoEvent(555L, 99L);

        listener.crearProductoInventario(event);

        ArgumentCaptor<CrearInventarioCommand> captor = ArgumentCaptor.forClass(CrearInventarioCommand.class);
        verify(inventarioService, times(1)).crearInvenario(captor.capture());
        var cmd = captor.getValue();
        assertEquals(555L, cmd.productoId());
        assertEquals(99L, cmd.cantidad());
        verifyNoMoreInteractions(inventarioService);
        verifyNoInteractions(inventarioValidacionService);
    }

    @Test
    @DisplayName("validarInventario: delega en InventarioValidacionService con orden e items")
    void validarInventario_ok() throws Exception {
        var event = new OrdenCreadaEvent(
                1001L, 2002L, "CL 1 # 2-3",
                List.of(new OrdenCreadaEvent.ItemOrdenCreada(10L, 3))
        );

        listener.validarInventario(event);

        verify(inventarioValidacionService, times(1))
                .validarInventarioCreacionOrden(1001L, event.items());
        verifyNoMoreInteractions(inventarioValidacionService);
        verifyNoInteractions(inventarioService);
    }

    @Test
    @DisplayName("validarInventario: si el servicio lanza, se propaga la excepción")
    void validarInventario_fallaServicio_propagado() throws Exception {
        var event = new OrdenCreadaEvent(1L, 2L, "X", List.of());
        doThrow(new RuntimeException("boom"))
                .when(inventarioValidacionService).validarInventarioCreacionOrden(1L, event.items());

        assertThrows(RuntimeException.class, () -> listener.validarInventario(event));
    }
}
