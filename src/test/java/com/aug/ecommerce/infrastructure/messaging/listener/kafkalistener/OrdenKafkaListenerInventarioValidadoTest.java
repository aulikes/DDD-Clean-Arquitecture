package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.InventarioValidadoEvent;
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
class OrdenKafkaListenerInventarioValidadoTest {

    @Mock private OrdenValidacionService ordenValidacionService;
    @Mock private ObjectMapper objectMapper;
    @InjectMocks private OrdenKafkaListener listener;

    private InventarioValidadoEvent invOk;
    private ConsumerRecord<String, IntegrationEventWrapper<InventarioValidadoEvent>> record;

    @BeforeEach
    void setUp() {
        invOk = new InventarioValidadoEvent(14L);
        record = new ConsumerRecord<>("inventario.validado", 0, 0L, null,
                IntegrationEventWrapper.wrap(invOk, invOk.getEventType(), invOk.getVersion(), "t5", Instant.now()));
    }

    @Test
    void invocaServicioEnExito() {
        when(objectMapper.convertValue(any(), eq(InventarioValidadoEvent.class))).thenReturn(invOk);

        listener.inventarioDisponible(record);

        verify(ordenValidacionService).registrarValidacionExitosa(14L, ValidacionCrearOrden.STOCK);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    void noInvocaServicioSiFallaMapeo() {
        when(objectMapper.convertValue(any(), eq(InventarioValidadoEvent.class))).thenThrow(new RuntimeException("mapper"));

        assertDoesNotThrow(() -> listener.inventarioDisponible(record));
        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    void manejaPayloadNulo() {
        var recNull = new ConsumerRecord<String, IntegrationEventWrapper<InventarioValidadoEvent>>(
                "inventario.validado", 0, 1L, null, null);

        assertDoesNotThrow(() -> listener.inventarioDisponible(recNull));
        verifyNoInteractions(ordenValidacionService);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void capturaExcepcionDelServicio() {
        when(objectMapper.convertValue(any(), eq(InventarioValidadoEvent.class))).thenReturn(invOk);
        doThrow(new RuntimeException("svc")).when(ordenValidacionService)
                .registrarValidacionExitosa(14L, ValidacionCrearOrden.STOCK);

        assertDoesNotThrow(() -> listener.inventarioDisponible(record));
        verify(ordenValidacionService).registrarValidacionExitosa(14L, ValidacionCrearOrden.STOCK);
    }
}
