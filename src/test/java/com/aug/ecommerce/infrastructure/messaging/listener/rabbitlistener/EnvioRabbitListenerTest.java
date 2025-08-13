package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.events.OrdenPagadaEvent;
import com.aug.ecommerce.application.services.EnvioService;
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

/**
 * Pruebas unitarias para EnvioRabbitListener.
 * - Mocks declarados afuera (@Mock) y el SUT con @InjectMocks.
 * - No usa MockBean ni contexto de Spring.
 * - Cubre ruta feliz, evento desconocido, JSON inválido y excepción del servicio.
 */
@ExtendWith(MockitoExtension.class)
class EnvioRabbitListenerTest {

    @Mock
    private EnvioService envioService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EnvioRabbitListener listener;

    @Test
    @DisplayName("prepararEnvio: procesa 'orden.envio.preparar' e invoca crearEnvio con datos correctos")
    void prepararEnvio_eventoValido_invocaServicio() throws Exception {
        // Se prepara un evento de orden pagada con los datos esperados por el servicio.
        var event = new OrdenPagadaEvent(1001L, "CL 1 # 2-3");
        var wrapper = IntegrationEventWrapper.wrap(
                event, event.getEventType(), event.getVersion(), "trace-123", Instant.now()
        );

        // Se simula el mapeo del payload al wrapper y del data al evento de dominio.
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(OrdenPagadaEvent.class))).thenReturn(event);

        // Se ejecuta el listener con cualquier payload (el mapper simulado retornará el wrapper).
        listener.prepararEnvio("{json}");

        // Se verifica que el servicio sea invocado con los valores del evento.
        verify(envioService, times(1)).crearEnvio(1001L, "CL 1 # 2-3");
        verifyNoMoreInteractions(envioService);
    }

    @Test
    @DisplayName("prepararEnvio: evento desconocido no invoca el servicio")
    void prepararEnvio_eventoDesconocido_noInvocaServicio() throws Exception {
        // Se arma un wrapper con un tipo de evento que el listener no reconoce.
        var wrapper = IntegrationEventWrapper.wrap(
                new Object(), "orden.evento.desconocido", "v1", "trace-x", Instant.now()
        );

        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);

        // Se invoca el listener; no debe llamar al servicio al no coincidir el tipo de evento.
        listener.prepararEnvio("{json}");

        verifyNoInteractions(envioService);
    }

    @Test
    @DisplayName("prepararEnvio: JSON inválido no lanza excepción y no invoca el servicio")
    void prepararEnvio_jsonInvalido_noLanza_noInvocaServicio() throws Exception {
        // El ObjectMapper falla al parsear el payload; el listener captura la excepción.
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class)))
                .thenThrow(new RuntimeException("mapper error"));

        // No debe propagar la excepción hacia el test.
        assertDoesNotThrow(() -> listener.prepararEnvio("{json-malformado"));

        // No debe invocar el servicio si no pudo mapear el payload.
        verifyNoInteractions(envioService);
    }

    @Test
    @DisplayName("prepararEnvio: si el servicio lanza excepción, el listener la captura (no propaga)")
    void prepararEnvio_servicioLanzaExcepcion_noPropaga() throws Exception {
        var event = new OrdenPagadaEvent(2002L, "CL 9 # 9-99");
        var wrapper = IntegrationEventWrapper.wrap(
                event, event.getEventType(), event.getVersion(), "trace-y", Instant.now()
        );

        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(OrdenPagadaEvent.class))).thenReturn(event);
        doThrow(new RuntimeException("service error"))
                .when(envioService).crearEnvio(2002L, "CL 9 # 9-99");

        // No debe propagar la excepción lanzada por el servicio.
        assertDoesNotThrow(() -> listener.prepararEnvio("{json}"));

        // Debe haberse intentado llamar al servicio exactamente una vez.
        verify(envioService, times(1)).crearEnvio(2002L, "CL 9 # 9-99");
    }
}
