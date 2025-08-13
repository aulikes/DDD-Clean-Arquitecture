package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.services.ClienteValidacionService;
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
 * Pruebas unitarias para ClienteKafkaListener usando Mockito (sin @MockBean).
 * Se valida el flujo principal y el manejo de excepciones interno.
 */
@ExtendWith(MockitoExtension.class)
class ClienteKafkaListenerTest {

    @Mock
    private ClienteValidacionService service;

    @Mock
    private ObjectMapper objectMapper;

    /**
     * Se inyectan los mocks en el listener. Se asume que la clase tiene
     * constructor para (ClienteValidacionService, ObjectMapper) por @RequiredArgsConstructor.
     */
    @InjectMocks
    private ClienteKafkaListener listener;

    private OrdenCreadaEvent event;
    private IntegrationEventWrapper<OrdenCreadaEvent> wrapper;
    private ConsumerRecord<String, IntegrationEventWrapper<OrdenCreadaEvent>> record;

    @BeforeEach
    void setUp() {
        // Se define un evento de ejemplo: contiene los IDs necesarios para la validación.
        event = new OrdenCreadaEvent(123L, 456L, "Calle 1 # 2-3", java.util.List.of());

        // Se envuelve el evento en el wrapper estándar usado por la app.
        wrapper = IntegrationEventWrapper.wrap(
                event,
                event.getEventType(),
                event.getVersion(),
                "trace-123",
                Instant.now()
        );

        // Se construye un ConsumerRecord simple para simular el mensaje de Kafka.
        record = new ConsumerRecord<>("orden.multicast.creada", 0, 0L, null, wrapper);
    }

    @Test
    @DisplayName("Debe invocar validarClienteCreacionOrden cuando el mensaje es válido")
    void shouldInvokeServiceWhenMessageIsValid() throws Exception {
        // Se configura el ObjectMapper para devolver el mismo evento esperado.
        when(objectMapper.convertValue(any(), eq(OrdenCreadaEvent.class))).thenReturn(event);

        // Se ejecuta el método bajo prueba.
        listener.onMessage(record);

        // Se verifica que el servicio haya sido invocado con los IDs correctos.
        verify(service, times(1)).validarClienteCreacionOrden(123L, 456L);

        // No debe haber más interacciones innecesarias.
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("No debe invocar el servicio si el mapeo del evento falla")
    void shouldNotInvokeServiceWhenMapperFails() {
        // Se simula un fallo en la conversión (por ejemplo, payload corrupto).
        when(objectMapper.convertValue(any(), eq(OrdenCreadaEvent.class)))
                .thenThrow(new RuntimeException("Error al convertir"));

        // Se ejecuta el método; no debe propagar la excepción.
        listener.onMessage(record);

        // Se verifica que el servicio NO se haya invocado.
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Debe capturar excepción lanzada por el servicio sin propagarla")
    void shouldCatchExceptionThrownByService() throws Exception {
        // El mapper retorna un evento válido.
        when(objectMapper.convertValue(any(), eq(OrdenCreadaEvent.class))).thenReturn(event);

        // El servicio lanza una excepción al validar.
        doThrow(new RuntimeException("Fallo en validación"))
                .when(service).validarClienteCreacionOrden(123L, 456L);

        // Se ejecuta el método; la excepción se captura internamente.
        listener.onMessage(record);

        // Se verifica que hubo intento de validación exactamente una vez.
        verify(service, times(1)).validarClienteCreacionOrden(123L, 456L);
        verifyNoMoreInteractions(service);
    }
}
