package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.ClienteValidadoEvent;
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
class OrdenKafkaListenerClienteValidadoTest {

    @Mock private OrdenValidacionService ordenValidacionService;
    @Mock private ObjectMapper objectMapper;
    @InjectMocks private OrdenKafkaListener listener;

    private ClienteValidadoEvent clienteOk;
    private ConsumerRecord<String, IntegrationEventWrapper<ClienteValidadoEvent>> record;

    @BeforeEach
    void setUp() {
        // Se crea un evento mínimo y su record.
        clienteOk = new ClienteValidadoEvent(10L);
        record = new ConsumerRecord<>("cliente.validado", 0, 0L, null,
                IntegrationEventWrapper.wrap(clienteOk, clienteOk.getEventType(), clienteOk.getVersion(), "t1", Instant.now()));
    }

    @Test
    void invocaServicioEnExito() {
        when(objectMapper.convertValue(any(), eq(ClienteValidadoEvent.class))).thenReturn(clienteOk);

        listener.clienteValidado(record);

        verify(ordenValidacionService).registrarValidacionExitosa(10L, ValidacionCrearOrden.CLIENTE);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    void noInvocaServicioSiFallaMapeo() {
        when(objectMapper.convertValue(any(), eq(ClienteValidadoEvent.class))).thenThrow(new RuntimeException("mapper"));

        assertDoesNotThrow(() -> listener.clienteValidado(record));
        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    void manejaPayloadNulo() {
        var recNull = new ConsumerRecord<String, IntegrationEventWrapper<ClienteValidadoEvent>>(
                "cliente.validado", 0, 1L, null, null);

        assertDoesNotThrow(() -> listener.clienteValidado(recNull));
        verifyNoInteractions(ordenValidacionService);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void capturaExcepcionDelServicio() {
        when(objectMapper.convertValue(any(), eq(ClienteValidadoEvent.class))).thenReturn(clienteOk);
        doThrow(new RuntimeException("svc")).when(ordenValidacionService)
                .registrarValidacionExitosa(10L, ValidacionCrearOrden.CLIENTE);

        assertDoesNotThrow(() -> listener.clienteValidado(record));
        verify(ordenValidacionService).registrarValidacionExitosa(10L, ValidacionCrearOrden.CLIENTE);
    }
}
