package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.InventarioNoValidadoEvent;
import com.aug.ecommerce.application.services.OrdenValidacionService;
import com.aug.ecommerce.application.services.ValidacionCrearOrden;
import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenKafkaListenerInventarioNoValidadoTest {

    @Mock private OrdenValidacionService ordenValidacionService;
    @Mock private ObjectMapper objectMapper;
    @InjectMocks private OrdenKafkaListener listener;

    private InventarioNoValidadoEvent invFail;
    private ConsumerRecord<String, IntegrationEventWrapper<InventarioNoValidadoEvent>> record;

    @BeforeEach
    void setUp() {
        invFail = new InventarioNoValidadoEvent(15L);
        record = new ConsumerRecord<>("inventario.no-validado", 0, 0L, null,
                IntegrationEventWrapper.wrap(invFail, invFail.getEventType(), invFail.getVersion(), "t6", Instant.now()));
    }

    @Test
    void invocaServicioEnFallo() {
        when(objectMapper.convertValue(any(), eq(InventarioNoValidadoEvent.class))).thenReturn(invFail);

        listener.inventarioNoDisponible(record);

        verify(ordenValidacionService).registrarValidacionFallida(15L, ValidacionCrearOrden.STOCK);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    void noInvocaServicioSiFallaMapeo() {
        when(objectMapper.convertValue(any(), eq(InventarioNoValidadoEvent.class))).thenThrow(new RuntimeException("mapper"));

        assertDoesNotThrow(() -> listener.inventarioNoDisponible(record));
        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    void manejaPayloadNulo() {
        var recNull = new ConsumerRecord<String, IntegrationEventWrapper<InventarioNoValidadoEvent>>(
                "inventario.no-validado", 0, 1L, null, null);

        assertDoesNotThrow(() -> listener.inventarioNoDisponible(recNull));
        verifyNoInteractions(ordenValidacionService);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void capturaExcepcionDelServicio() {
        when(objectMapper.convertValue(any(), eq(InventarioNoValidadoEvent.class))).thenReturn(invFail);
        doThrow(new RuntimeException("svc")).when(ordenValidacionService)
                .registrarValidacionFallida(15L, ValidacionCrearOrden.STOCK);

        assertDoesNotThrow(() -> listener.inventarioNoDisponible(record));
        verify(ordenValidacionService).registrarValidacionFallida(15L, ValidacionCrearOrden.STOCK);
    }
}
