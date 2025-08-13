package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.commands.CrearInventarioCommand;
import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.events.ProductoCreadoEvent;
import com.aug.ecommerce.application.services.InventarioService;
import com.aug.ecommerce.application.services.InventarioValidacionService;
import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para InventarioRabbitListener.
 * - Mocks declarados afuera (@Mock) y el SUT con @InjectMocks.
 * - No usa MockBean ni contexto de Spring.
 * - Cubre ruta feliz, evento desconocido, JSON inválido y excepción del servicio
 *   para ambos métodos: validarInventario() y crearInventario().
 */
@ExtendWith(MockitoExtension.class)
class InventarioRabbitListenerTest {

    // -------- Mocks externos --------
    @Mock
    private InventarioService inventarioService;

    @Mock
    private InventarioValidacionService inventarioValidacionService;

    @Mock
    private ObjectMapper objectMapper;

    // -------- Clase bajo prueba --------
    @InjectMocks
    private InventarioRabbitListener listener;

    // =========================
    // validarInventario(String)
    // =========================

    @Test
    @DisplayName("validarInventario: procesa 'orden.multicast.creada' e invoca validarInventarioCreacionOrden")
    void validarInventario_eventoValido_invocaServicio() throws Exception {
        // Evento con ítems representativos
        var ordenEvent = new OrdenCreadaEvent(
                1001L,
                2002L,
                "CL 1 # 2-3",
                List.of(
                        new OrdenCreadaEvent.ItemOrdenCreada(10L, 3),
                        new OrdenCreadaEvent.ItemOrdenCreada(20L, 1)
                )
        );
        // Wrapper genérico esperado por el listener
        var wrapper = IntegrationEventWrapper.wrap(
                ordenEvent, ordenEvent.getEventType(), ordenEvent.getVersion(), "trace-x", Instant.now()
        );

        // Se simula parseo de JSON y conversión al evento real
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(OrdenCreadaEvent.class))).thenReturn(ordenEvent);

        // Ejecución
        listener.validarInventario("{json}");

        // Verificación: se invoca el servicio de validación con los datos correctos
        verify(inventarioValidacionService, times(1))
                .validarInventarioCreacionOrden(eq(1001L), eq(ordenEvent.items()));
        verifyNoMoreInteractions(inventarioValidacionService);
        verifyNoInteractions(inventarioService);
    }

    @Test
    @DisplayName("validarInventario: evento desconocido no invoca servicios")
    void validarInventario_eventoDesconocido_noInvocaServicios() throws Exception {
        // Wrapper con tipo de evento que no aplica
        var wrapper = IntegrationEventWrapper.wrap(
                new Object(), "orden.evento.desconocido", "v1", "trace-y", Instant.now()
        );

        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);

        // Ejecución
        listener.validarInventario("{json}");

        // Verificación: no hay interacciones con servicios
        verifyNoInteractions(inventarioValidacionService);
        verifyNoInteractions(inventarioService);
    }

    @Test
    @DisplayName("validarInventario: JSON inválido no lanza excepción y no invoca servicios")
    void validarInventario_jsonInvalido_noLanza_noInvocaServicios() throws Exception {
        // Simulación de error de mapeo
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class)))
                .thenThrow(new RuntimeException("mapper error"));

        // No debe propagarse la excepción
        assertDoesNotThrow(() -> listener.validarInventario("{json-malformado"));

        // Verificación: no hay interacciones con servicios
        verifyNoInteractions(inventarioValidacionService);
        verifyNoInteractions(inventarioService);
    }

    @Test
    @DisplayName("validarInventario: excepción del servicio es capturada (no se propaga)")
    void validarInventario_servicioLanzaExcepcion_noPropaga() throws Exception {
        var ordenEvent = new OrdenCreadaEvent(
                1001L, 2002L, "CL 1 # 2-3",
                List.of(new OrdenCreadaEvent.ItemOrdenCreada(10L, 3))
        );
        var wrapper = IntegrationEventWrapper.wrap(
                ordenEvent, ordenEvent.getEventType(), ordenEvent.getVersion(), "trace-z", Instant.now()
        );

        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(OrdenCreadaEvent.class))).thenReturn(ordenEvent);
        doThrow(new RuntimeException("service error"))
                .when(inventarioValidacionService).validarInventarioCreacionOrden(eq(1001L), eq(ordenEvent.items()));

        // No debe lanzar excepción hacia afuera
        assertDoesNotThrow(() -> listener.validarInventario("{json}"));

        // Verificación: el servicio fue invocado exactamente una vez
        verify(inventarioValidacionService, times(1))
                .validarInventarioCreacionOrden(eq(1001L), eq(ordenEvent.items()));
        verifyNoInteractions(inventarioService);
    }

    // ======================
    // crearInventario(String)
    // ======================

    @Test
    @DisplayName("crearInventario: procesa 'producto.inventario.crear' e invoca crearInvenario con el comando correcto")
    void crearInventario_eventoValido_invocaServicio() throws Exception {
        // Evento de producto creado
        var productoEvent = new ProductoCreadoEvent(555L, 99L);
        var wrapper = IntegrationEventWrapper.wrap(
                productoEvent, productoEvent.getEventType(), productoEvent.getVersion(), "trace-p", Instant.now()
        );

        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(ProductoCreadoEvent.class))).thenReturn(productoEvent);

        // Ejecución
        listener.crearInventario("{json}");

        // Se captura el command para validar sus campos
        ArgumentCaptor<CrearInventarioCommand> captor = ArgumentCaptor.forClass(CrearInventarioCommand.class);
        verify(inventarioService, times(1)).crearInvenario(captor.capture());

        var cmd = captor.getValue();
        assertEquals(555L, cmd.productoId());
        assertEquals(99L, cmd.cantidad());

        verifyNoMoreInteractions(inventarioService);
        verifyNoInteractions(inventarioValidacionService);
    }

    @Test
    @DisplayName("crearInventario: evento desconocido no invoca servicios")
    void crearInventario_eventoDesconocido_noInvocaServicios() throws Exception {
        var wrapper = IntegrationEventWrapper.wrap(
                new Object(), "producto.evento.desconocido", "v1", "trace-q", Instant.now()
        );

        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);

        listener.crearInventario("{json}");

        verifyNoInteractions(inventarioService);
        verifyNoInteractions(inventarioValidacionService);
    }

    @Test
    @DisplayName("crearInventario: JSON inválido no lanza excepción y no invoca servicios")
    void crearInventario_jsonInvalido_noLanza_noInvocaServicios() throws Exception {
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class)))
                .thenThrow(new RuntimeException("mapper error"));

        assertDoesNotThrow(() -> listener.crearInventario("{json-malformado"));

        verifyNoInteractions(inventarioService);
        verifyNoInteractions(inventarioValidacionService);
    }

    @Test
    @DisplayName("crearInventario: excepción del servicio es capturada (no se propaga)")
    void crearInventario_servicioLanzaExcepcion_noPropaga() throws Exception {
        var productoEvent = new ProductoCreadoEvent(123L, 5L);
        var wrapper = IntegrationEventWrapper.wrap(
                productoEvent, productoEvent.getEventType(), productoEvent.getVersion(), "trace-r", Instant.now()
        );

        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(ProductoCreadoEvent.class))).thenReturn(productoEvent);
        doThrow(new RuntimeException("service error"))
                .when(inventarioService).crearInvenario(any(CrearInventarioCommand.class));

        assertDoesNotThrow(() -> listener.crearInventario("{json}"));

        verify(inventarioService, times(1)).crearInvenario(any(CrearInventarioCommand.class));
        verifyNoInteractions(inventarioValidacionService);
    }
}
