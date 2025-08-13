package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.OrdenPagadaEvent;
import com.aug.ecommerce.application.services.EnvioService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para EnvioKafkaListener.
 * Se cubren flujos de éxito, fallas en mapeo del payload, excepciones del servicio
 * y manejo defensivo de payloads nulos.
 */
@ExtendWith(MockitoExtension.class)
class EnvioKafkaListenerTest {

    @Mock
    private EnvioService service;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EnvioKafkaListener listener;

    private OrdenPagadaEvent event;
    private IntegrationEventWrapper<OrdenPagadaEvent> wrapper;
    private ConsumerRecord<String, IntegrationEventWrapper<OrdenPagadaEvent>> record;

    @BeforeEach
    void setUp() {
        // Se define un evento válido representativo.
        event = new OrdenPagadaEvent(321L, "Cra 7 # 8-90");

        // Se envuelve el evento con metadatos estándar.
        wrapper = IntegrationEventWrapper.wrap(
                event,
                event.getEventType(),
                event.getVersion(),
                "trace-abc",
                Instant.now()
        );

        // Se crea un ConsumerRecord que simula el mensaje recibido desde Kafka.
        record = new ConsumerRecord<>("orden.pagada", 0, 0L, null, wrapper);
    }

    @Test
    @DisplayName("Invoca crearEnvio cuando el evento es válido")
    void shouldCreateShipmentWhenEventIsValid() {
        // El ObjectMapper convierte exitosamente el payload al tipo esperado.
        when(objectMapper.convertValue(any(), eq(OrdenPagadaEvent.class))).thenReturn(event);

        // Ejecución del listener.
        listener.onMessage(record);

        // Verificación: el servicio debe recibir los valores correctos.
        verify(service, times(1)).crearEnvio(321L, "Cra 7 # 8-90");
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("No invoca el servicio cuando falla la conversión del payload")
    void shouldNotCallServiceWhenMappingFails() {
        // El ObjectMapper falla al convertir el payload.
        when(objectMapper.convertValue(any(), eq(OrdenPagadaEvent.class)))
                .thenThrow(new RuntimeException("conversion error"));

        // Ejecución del listener. La excepción se captura internamente.
        listener.onMessage(record);

        // Verificación: no debe llamar al servicio.
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Captura excepción lanzada por el servicio sin propagarla")
    void shouldCatchServiceException() {
        // Conversión correcta del payload.
        when(objectMapper.convertValue(any(), eq(OrdenPagadaEvent.class))).thenReturn(event);

        // El servicio lanza una excepción durante la creación de envío.
        doThrow(new RuntimeException("service error"))
                .when(service).crearEnvio(321L, "Cra 7 # 8-90");

        // Ejecución del listener.
        listener.onMessage(record);

        // Verificación: se intentó invocar al servicio exactamente una vez.
        verify(service, times(1)).crearEnvio(321L, "Cra 7 # 8-90");
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("Maneja payload nulo sin invocar el servicio")
    void shouldHandleNullPayloadSafely() {
        // Se crea un record con valor nulo para simular un mensaje malformado.
        ConsumerRecord<String, IntegrationEventWrapper<OrdenPagadaEvent>> nullRecord =
                new ConsumerRecord<>("orden.pagada", 0, 1L, null, null);

        // Ejecución del listener; cualquier NPE debe ser capturada internamente.
        listener.onMessage(nullRecord);

        // Verificación: no se produce interacción con el servicio ni con el mapper.
        verifyNoInteractions(service);
        verifyNoInteractions(objectMapper);
    }
}
