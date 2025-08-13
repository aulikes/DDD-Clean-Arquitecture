package com.aug.ecommerce.infrastructure.messaging.listener.rabbitlistener;

import com.aug.ecommerce.application.events.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.services.PagoService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PagoRabbitListener.
 * - Mocks declarados afuera (@Mock) y el SUT con @InjectMocks.
 * - No usa MockBean ni contexto de Spring.
 * - Cubre ruta feliz, evento desconocido, JSON inválido y excepción del servicio.
 */
@ExtendWith(MockitoExtension.class)
class PagoRabbitListenerTest {

    @Mock
    private PagoService pagoService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PagoRabbitListener listener;

    @Mock
    private OrdenPreparadaParaPagoEvent ordenPreparadaEvent;

    @Test
    @DisplayName("realizarPagoDeOrden: procesa 'orden.pago.solicitar' e invoca pagoService.realizarPago(event)")
    void realizarPagoDeOrden_eventoValido_invocaServicio() throws Exception {
        // Wrapper esperado por el listener con el eventType correcto
        var wrapper = IntegrationEventWrapper.wrap(
                ordenPreparadaEvent, "orden.pago.solicitar", "v1", "trace-123", Instant.now()
        );

        // Stubs del mapper
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(OrdenPreparadaParaPagoEvent.class)))
                .thenReturn(ordenPreparadaEvent);

        // Act
        listener.realizarPagoDeOrden("{json}");

        // Assert
        verify(pagoService, times(1)).realizarPago(ordenPreparadaEvent);
        verifyNoMoreInteractions(pagoService);
    }

    @Test
    @DisplayName("realizarPagoDeOrden: evento desconocido no invoca el servicio")
    void realizarPagoDeOrden_eventoDesconocido_noInvocaServicio() throws Exception {
        var wrapper = IntegrationEventWrapper.wrap(
                new Object(), "orden.evento.desconocido", "v1", "trace-x", Instant.now()
        );
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);

        listener.realizarPagoDeOrden("{json}");

        verifyNoInteractions(pagoService);
        // Opcional: confirmar que no intenta convertir el 'data' a OrdenPreparadaParaPagoEvent
        verify(objectMapper, never()).convertValue(any(), eq(OrdenPreparadaParaPagoEvent.class));
    }

    @Test
    @DisplayName("realizarPagoDeOrden: JSON inválido no lanza excepción y no invoca el servicio")
    void realizarPagoDeOrden_jsonInvalido_noLanza_noInvocaServicio() throws Exception {
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class)))
                .thenThrow(new RuntimeException("mapper error"));

        assertDoesNotThrow(() -> listener.realizarPagoDeOrden("{json-malformado"));

        verifyNoInteractions(pagoService);
    }

    @Test
    @DisplayName("realizarPagoDeOrden: si el servicio lanza excepción, el listener la captura (no propaga)")
    void realizarPagoDeOrden_servicioLanza_noPropaga() throws Exception {
        var wrapper = IntegrationEventWrapper.wrap(
                ordenPreparadaEvent, "orden.pago.solicitar", "v1", "trace-y", Instant.now()
        );
        when(objectMapper.readValue(anyString(), eq(IntegrationEventWrapper.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(eq(wrapper.data()), eq(OrdenPreparadaParaPagoEvent.class)))
                .thenReturn(ordenPreparadaEvent);
        doThrow(new RuntimeException("service error")).when(pagoService).realizarPago(ordenPreparadaEvent);

        assertDoesNotThrow(() -> listener.realizarPagoDeOrden("{json}"));

        verify(pagoService, times(1)).realizarPago(ordenPreparadaEvent);
    }
}
