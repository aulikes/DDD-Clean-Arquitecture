package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.PagoConfirmadoEvent;
import com.aug.ecommerce.application.services.OrdenValidacionService;
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
class OrdenKafkaListenerPagoRealizadoTest {

    @Mock private OrdenValidacionService ordenValidacionService;
    @Mock private ObjectMapper objectMapper;
    @InjectMocks private OrdenKafkaListener listener;

    private PagoConfirmadoEvent pago;
    private ConsumerRecord<String, IntegrationEventWrapper<PagoConfirmadoEvent>> record;

    @BeforeEach
    void setUp() {
        pago = new PagoConfirmadoEvent(16L, 1000L, Instant.now(), true, "TX-OK", null);
        record = new ConsumerRecord<>("pago.realizado", 0, 0L, null,
                IntegrationEventWrapper.wrap(pago, pago.getEventType(), pago.getVersion(), "t7", Instant.now()));
    }

    @Test
    void invocaGestionPagoEnExito() {
        when(objectMapper.convertValue(any(), eq(PagoConfirmadoEvent.class))).thenReturn(pago);

        listener.pagoRealizado(record);

        verify(ordenValidacionService).gestionarInformacionPago(pago);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    void noInvocaServicioSiFallaMapeo() {
        when(objectMapper.convertValue(any(), eq(PagoConfirmadoEvent.class))).thenThrow(new RuntimeException("mapper"));

        assertDoesNotThrow(() -> listener.pagoRealizado(record));
        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    void manejaPayloadNulo() {
        var recNull = new ConsumerRecord<String, IntegrationEventWrapper<PagoConfirmadoEvent>>(
                "pago.realizado", 0, 1L, null, null);

        assertDoesNotThrow(() -> listener.pagoRealizado(recNull));
        verifyNoInteractions(ordenValidacionService);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void capturaExcepcionDelServicio() {
        when(objectMapper.convertValue(any(), eq(PagoConfirmadoEvent.class))).thenReturn(pago);
        doThrow(new RuntimeException("svc")).when(ordenValidacionService).gestionarInformacionPago(any(PagoConfirmadoEvent.class));

        assertDoesNotThrow(() -> listener.pagoRealizado(record));
        verify(ordenValidacionService).gestionarInformacionPago(pago);
    }
}
