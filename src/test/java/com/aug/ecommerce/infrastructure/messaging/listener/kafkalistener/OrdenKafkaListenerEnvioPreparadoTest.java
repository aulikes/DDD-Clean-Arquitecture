package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.EnvioPreparadoEvent;
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
class OrdenKafkaListenerEnvioPreparadoTest {

    @Mock private OrdenValidacionService ordenValidacionService;
    @Mock private ObjectMapper objectMapper;
    @InjectMocks private OrdenKafkaListener listener;

    private EnvioPreparadoEvent envio;
    private ConsumerRecord<String, IntegrationEventWrapper<EnvioPreparadoEvent>> record;

    @BeforeEach
    void setUp() {
        envio = new EnvioPreparadoEvent(17L, 2000L, Instant.now(), true, "ENV-OK", null);
        record = new ConsumerRecord<>("envio.preparado", 0, 0L, null,
                IntegrationEventWrapper.wrap(envio, envio.getEventType(), envio.getVersion(), "t8", Instant.now()));
    }

    @Test
    void invocaGestionEnvioEnExito() {
        when(objectMapper.convertValue(any(), eq(EnvioPreparadoEvent.class))).thenReturn(envio);

        listener.envioPreparado(record);

        verify(ordenValidacionService).gestionarInformacionEnvio(envio);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    void noInvocaServicioSiFallaMapeo() {
        when(objectMapper.convertValue(any(), eq(EnvioPreparadoEvent.class))).thenThrow(new RuntimeException("mapper"));

        assertDoesNotThrow(() -> listener.envioPreparado(record));
        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    void manejaPayloadNulo() {
        var recNull = new ConsumerRecord<String, IntegrationEventWrapper<EnvioPreparadoEvent>>(
                "envio.preparado", 0, 1L, null, null);

        assertDoesNotThrow(() -> listener.envioPreparado(recNull));
        verifyNoInteractions(ordenValidacionService);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void capturaExcepcionDelServicio() {
        when(objectMapper.convertValue(any(), eq(EnvioPreparadoEvent.class))).thenReturn(envio);
        doThrow(new RuntimeException("svc")).when(ordenValidacionService).gestionarInformacionEnvio(any(EnvioPreparadoEvent.class));

        assertDoesNotThrow(() -> listener.envioPreparado(record));
        verify(ordenValidacionService).gestionarInformacionEnvio(envio);
    }
}
