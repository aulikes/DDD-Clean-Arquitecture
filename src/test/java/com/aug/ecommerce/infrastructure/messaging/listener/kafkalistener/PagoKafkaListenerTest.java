package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.services.PagoService;
import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para PagoKafkaListener.
 * Se validan los escenarios principales del método onMessage.
 */
@ExtendWith(MockitoExtension.class)
class PagoKafkaListenerTest {

    @Mock
    private PagoService pagoService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PagoKafkaListener listener;

    private OrdenPreparadaParaPagoEvent event;
    private ConsumerRecord<String, IntegrationEventWrapper<OrdenPreparadaParaPagoEvent>> record;

    @BeforeEach
    void setUp() {
        // Se define un evento representativo para la solicitud de pago.
        event = new OrdenPreparadaParaPagoEvent(99L, 149_900.0, "TARJETA");

        // Se crea el wrapper con metadatos estándar y se arma el record de Kafka.
        IntegrationEventWrapper<OrdenPreparadaParaPagoEvent> wrapper = IntegrationEventWrapper.wrap(
                event, event.getEventType(), event.getVersion(), "trace-pago-1", Instant.now()
        );
        record = new ConsumerRecord<>("orden.pago.solicitar", 0, 0L, null, wrapper);
    }

    @Test
    @DisplayName("Invoca pagoService.realizarPago cuando el payload es válido")
    void shouldCallServiceWhenPayloadIsValid() {
        // El mapper convierte exitosamente el contenido del wrapper al tipo esperado.
        when(objectMapper.convertValue(any(), eq(OrdenPreparadaParaPagoEvent.class))).thenReturn(event);

        // Ejecución del método bajo prueba.
        listener.onMessage(record);

        // Verificación: debe invocar al servicio con el evento mapeado.
        verify(pagoService, times(1)).realizarPago(event);
        verifyNoMoreInteractions(pagoService);
    }

    @Test
    @DisplayName("No invoca el servicio cuando falla la conversión del payload")
    void shouldNotCallServiceWhenMappingFails() {
        // El mapper falla al convertir el contenido del wrapper.
        when(objectMapper.convertValue(any(), eq(OrdenPreparadaParaPagoEvent.class)))
                .thenThrow(new RuntimeException("mapper error"));

        // El listener captura la excepción y no la propaga.
        assertDoesNotThrow(() -> listener.onMessage(record));

        // Verificación: no se debe invocar al servicio.
        verifyNoInteractions(pagoService);
    }

    @Test
    @DisplayName("Maneja record con valor nulo sin invocar el servicio")
    void shouldHandleNullRecordValueSafely() {
        // Se simula un record con value nulo (mensaje malformado).
        ConsumerRecord<String, IntegrationEventWrapper<OrdenPreparadaParaPagoEvent>> nullRecord =
                new ConsumerRecord<>("orden.pago.solicitar", 0, 1L, null, null);

        // El listener debe manejarlo sin lanzar excepciones.
        assertDoesNotThrow(() -> listener.onMessage(nullRecord));

        // Verificación: no hay interacciones con el servicio ni con el mapper.
        verifyNoInteractions(pagoService);
        verifyNoInteractions(objectMapper);
    }

    @Test
    @DisplayName("Captura excepción lanzada por el servicio sin propagarla")
    void shouldCatchServiceException() {
        // El mapper retorna un evento válido.
        when(objectMapper.convertValue(any(), eq(OrdenPreparadaParaPagoEvent.class))).thenReturn(event);

        // El servicio lanza una excepción durante el procesamiento.
        doThrow(new RuntimeException("service error"))
                .when(pagoService).realizarPago(event);

        // El listener captura la excepción y continúa.
        assertDoesNotThrow(() -> listener.onMessage(record));

        // Verificación: el intento de pago se realizó exactamente una vez.
        verify(pagoService, times(1)).realizarPago(event);
        verifyNoMoreInteractions(pagoService);
    }
}
