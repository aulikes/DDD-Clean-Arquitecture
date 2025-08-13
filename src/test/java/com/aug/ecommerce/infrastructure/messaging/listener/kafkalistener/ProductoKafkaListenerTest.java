package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.services.ProductoValidacionService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductoKafkaListener.
 * Se valida el flujo principal y casos de borde: fallo de mapeo, payload nulo y excepción del servicio.
 */
@ExtendWith(MockitoExtension.class)
class ProductoKafkaListenerTest {

    @Mock
    private ProductoValidacionService productoValidacionService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductoKafkaListener listener;

    private OrdenCreadaEvent ordenEvent;
    private ConsumerRecord<String, IntegrationEventWrapper<OrdenCreadaEvent>> record;

    @BeforeEach
    void setUp() {
        // Se modela una orden con dos ítems de producto.
        ordenEvent = new OrdenCreadaEvent(
                1001L,
                2002L,
                "Av. Siempre Viva 123",
                List.of(
                        new OrdenCreadaEvent.ItemOrdenCreada(10L, 2),
                        new OrdenCreadaEvent.ItemOrdenCreada(20L, 1)
                )
        );

        // Se envuelve el evento con metadatos estándar y se arma el record de Kafka.
        IntegrationEventWrapper<OrdenCreadaEvent> wrapper = IntegrationEventWrapper.wrap(
                ordenEvent, ordenEvent.getEventType(), ordenEvent.getVersion(), "trace-prod-1", Instant.now()
        );
        record = new ConsumerRecord<>("orden.multicast.creada", 0, 0L, null, wrapper);
    }

    @Test
    @DisplayName("Invoca validarProductoCreacionOrden cuando el payload es válido")
    void shouldValidateProductsWhenPayloadIsValid() {
        when(objectMapper.convertValue(any(), eq(OrdenCreadaEvent.class))).thenReturn(ordenEvent);

        listener.onMessage(record);

        verify(productoValidacionService, times(1))
                .validarProductoCreacionOrden(1001L, ordenEvent.items());
        verifyNoMoreInteractions(productoValidacionService);
    }

    @Test
    @DisplayName("No invoca el servicio cuando falla la conversión del payload")
    void shouldNotCallServiceWhenMappingFails() {
        when(objectMapper.convertValue(any(), eq(OrdenCreadaEvent.class)))
                .thenThrow(new RuntimeException("mapper error"));

        assertDoesNotThrow(() -> listener.onMessage(record));

        verifyNoInteractions(productoValidacionService);
    }

    @Test
    @DisplayName("Maneja record con valor nulo sin invocar el servicio")
    void shouldHandleNullRecordValueSafely() {
        ConsumerRecord<String, IntegrationEventWrapper<OrdenCreadaEvent>> nullRecord =
                new ConsumerRecord<>("orden.multicast.creada", 0, 1L, null, null);

        assertDoesNotThrow(() -> listener.onMessage(nullRecord));

        verifyNoInteractions(productoValidacionService);
        verifyNoInteractions(objectMapper);
    }

    @Test
    @DisplayName("Captura excepción del servicio sin propagarla")
    void shouldCatchServiceException() {
        when(objectMapper.convertValue(any(), eq(OrdenCreadaEvent.class))).thenReturn(ordenEvent);

        doThrow(new RuntimeException("service error"))
                .when(productoValidacionService).validarProductoCreacionOrden(1001L, ordenEvent.items());

        assertDoesNotThrow(() -> listener.onMessage(record));

        verify(productoValidacionService, times(1))
                .validarProductoCreacionOrden(1001L, ordenEvent.items());
        verifyNoMoreInteractions(productoValidacionService);
    }
}
