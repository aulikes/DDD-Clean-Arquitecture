package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.services.ClienteValidacionService;
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
 * Tests unitarios para ClienteRabbitListener.
 * - Mocks declarados afuera (@Mock) y el SUT con @InjectMocks.
 * - No usa MockBean ni contexto de Spring.
 * - Cubre ruta feliz, evento desconocido, JSON inválido y excepción del servicio.
 */
@ExtendWith(MockitoExtension.class)
class ClienteRabbitListenerTest {

    @Mock
    private ClienteValidacionService clienteValidacionService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ClienteRabbitListener listener;

    @Test
    @DisplayName("validarCliente: procesa 'orden.multicast.creada' e invoca el servicio con IDs correctos")
    void validarCliente_eventoValido_invocaServicio() throws Exception {
        // Se arma el evento de dominio esperado
        var ordenEvent = new OrdenCreadaEvent(
                10L,
                77L,
                "CL 1 # 2-3",
                List.of() // items no relevantes para este listener
        );
        // Wrapper genérico esperado por el listener
        var wrapper = IntegrationEventWrapper.wrap(
                ordenEvent, ordenEvent.getEventType(), ordenEvent.getVersion(), "trace-123", Instant.now()
        );

        // Se stubbean las llamadas al ObjectMapper (como se usa mock, no se parsea JSON real)
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(OrdenCreadaEvent.class))).thenReturn(ordenEvent);

        // Ejecución: el payload puede ser cualquier String; el mapper simulado devuelve el wrapper
        listener.validarCliente("{json}");

        // Verificación: el servicio debe ser invocado con los IDs del evento
        verify(clienteValidacionService, times(1)).validarClienteCreacionOrden(10L, 77L);
        verifyNoMoreInteractions(clienteValidacionService);
    }

    @Test
    @DisplayName("validarCliente: evento desconocido no invoca el servicio")
    void validarCliente_eventoDesconocido_noInvocaServicio() throws Exception {
        // Wrapper con eventType desconocido
        var wrapper = IntegrationEventWrapper.wrap(
                // El data puede ser un mapa o cualquier objeto; no se usará
                new Object(), "orden.evento.desconocido", "v1", "trace-xyz", Instant.now()
        );

        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);

        listener.validarCliente("{json}");

        // No debe invocar al servicio cuando el tipo de evento no coincide
        verifyNoInteractions(clienteValidacionService);
    }

    @Test
    @DisplayName("validarCliente: JSON inválido no lanza excepción y no invoca servicio")
    void validarCliente_jsonInvalido_noLanza_noInvocaServicio() throws Exception {
        // El mapper arroja excepción al intentar parsear
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class)))
                .thenThrow(new RuntimeException("mapper error"));

        // El listener captura la excepción y solo registra en logs
        assertDoesNotThrow(() -> listener.validarCliente("{json-malformado"));

        verifyNoInteractions(clienteValidacionService);
    }

    @Test
    @DisplayName("validarCliente: si el servicio lanza excepción, el listener la captura (no propaga)")
    void validarCliente_servicioLanzaExcepcion_noPropaga() throws Exception {
        var ordenEvent = new OrdenCreadaEvent(10L, 77L, "CL 1 # 2-3", List.of());
        var wrapper = IntegrationEventWrapper.wrap(
                ordenEvent, ordenEvent.getEventType(), ordenEvent.getVersion(), "trace-err", Instant.now()
        );

        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(OrdenCreadaEvent.class))).thenReturn(ordenEvent);
        doThrow(new RuntimeException("service error"))
                .when(clienteValidacionService).validarClienteCreacionOrden(10L, 77L);

        // No debe lanzar; el listener hace manejo interno
        assertDoesNotThrow(() -> listener.validarCliente("{json}"));

        // Se intentó invocar al servicio exactamente una vez
        verify(clienteValidacionService, times(1)).validarClienteCreacionOrden(10L, 77L);
    }
}
