package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.services.ProductoValidacionService;
import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ProductoRabbitListener.
 * - Mocks declarados afuera (@Mock) y el SUT con @InjectMocks.
 * - No usa @MockBean ni contexto de Spring.
 * - Cubre: ruta feliz, evento desconocido, JSON inválido y excepción del servicio.
 */
@ExtendWith(MockitoExtension.class)
class ProductoRabbitListenerTest {

    @Mock
    private ProductoValidacionService productoValidacionService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductoRabbitListener listener;

    @Test
    @DisplayName("validarProductoCreacionOrden: procesa 'orden.multicast.creada' e invoca el servicio con datos correctos")
    void validarProductoCreacionOrden_eventoValido_invocaServicio() throws Exception {
        // Arrange: evento de orden creada con ítems de ejemplo
        var evento = new OrdenCreadaEvent(
                1001L,
                2002L,
                "CL 1 # 2-3",
                List.of(
                        new OrdenCreadaEvent.ItemOrdenCreada(10L, 3),
                        new OrdenCreadaEvent.ItemOrdenCreada(20L, 1)
                )
        );

        // El listener espera un IntegrationEventWrapper con eventType = "orden.multicast.creada"
        var wrapper = IntegrationEventWrapper.wrap(
                evento, evento.getEventType(), evento.getVersion(), "trace-123", Instant.now()
        );

        // Stub: el mapper devuelve el wrapper y luego convierte el data al evento de dominio
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(OrdenCreadaEvent.class))).thenReturn(evento);

        // Act
        listener.validarProductoCreacionOrden("{json}");

        // Assert: el servicio debe recibir exactamente los IDs/ítems del evento
        verify(productoValidacionService, times(1))
                .validarProductoCreacionOrden(1001L, evento.items());
        verifyNoMoreInteractions(productoValidacionService);
    }

    @Test
    @DisplayName("validarProductoCreacionOrden: evento desconocido no invoca el servicio")
    void validarProductoCreacionOrden_eventoDesconocido_noInvocaServicio() throws Exception {
        // Arrange: wrapper con un tipo de evento que no reconoce el listener
        var wrapper = IntegrationEventWrapper.wrap(
                new Object(), "orden.evento.desconocido", "v1", "trace-x", Instant.now()
        );
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);

        // Act
        listener.validarProductoCreacionOrden("{json}");

        // Assert
        verifyNoInteractions(productoValidacionService);
        verify(objectMapper, never()).convertValue(any(), eq(OrdenCreadaEvent.class));
    }

    @Test
    @DisplayName("validarProductoCreacionOrden: JSON inválido no lanza excepción y no invoca el servicio")
    void validarProductoCreacionOrden_jsonInvalido_noLanza_noInvocaServicio() throws Exception {
        // Arrange: el ObjectMapper falla al parsear el payload
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class)))
                .thenThrow(new RuntimeException("mapper error"));

        // Act + Assert: el listener captura la excepción internamente
        assertDoesNotThrow(() -> listener.validarProductoCreacionOrden("{json-malformado"));

        verifyNoInteractions(productoValidacionService);
    }

    @Test
    @DisplayName("validarProductoCreacionOrden: si el servicio lanza excepción, el listener la captura (no propaga)")
    void validarProductoCreacionOrden_servicioLanza_noPropaga() throws Exception {
        // Arrange
        var evento = new OrdenCreadaEvent(
                3003L,
                4004L,
                "CL 9 # 9-99",
                List.of(new OrdenCreadaEvent.ItemOrdenCreada(99L, 2))
        );
        var wrapper = IntegrationEventWrapper.wrap(
                evento, evento.getEventType(), evento.getVersion(), "trace-y", Instant.now()
        );
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(OrdenCreadaEvent.class))).thenReturn(evento);

        // Forzamos excepción en el servicio
        doThrow(new RuntimeException("service error"))
                .when(productoValidacionService).validarProductoCreacionOrden(evento.ordenId(), evento.items());

        // Act + Assert: no debe propagar la excepción
        assertDoesNotThrow(() -> listener.validarProductoCreacionOrden("{json}"));

        // Se intentó invocar exactamente una vez
        verify(productoValidacionService, times(1))
                .validarProductoCreacionOrden(evento.ordenId(), evento.items());
    }
}
