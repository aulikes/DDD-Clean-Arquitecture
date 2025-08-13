package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.ClienteNoValidadoEvent;
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
class OrdenKafkaListenerClienteNoValidadoTest {

    @Mock private OrdenValidacionService ordenValidacionService;
    @Mock private ObjectMapper objectMapper;
    @InjectMocks private OrdenKafkaListener listener;

    private ClienteNoValidadoEvent clienteFail;
    private ConsumerRecord<String, IntegrationEventWrapper<ClienteNoValidadoEvent>> record;

    @BeforeEach
    void setUp() {
        clienteFail = new ClienteNoValidadoEvent(11L);
        record = new ConsumerRecord<>("cliente.no-validado", 0, 0L, null,
                IntegrationEventWrapper.wrap(clienteFail, clienteFail.getEventType(), clienteFail.getVersion(), "t2", Instant.now()));
    }

    @Test
    void invocaServicioEnFallo() {
        when(objectMapper.convertValue(any(), eq(ClienteNoValidadoEvent.class))).thenReturn(clienteFail);

        listener.clienteNoValidado(record);

        verify(ordenValidacionService).registrarValidacionFallida(11L, ValidacionCrearOrden.CLIENTE);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    void noInvocaServicioSiFallaMapeo() {
        when(objectMapper.convertValue(any(), eq(ClienteNoValidadoEvent.class))).thenThrow(new RuntimeException("mapper"));

        assertDoesNotThrow(() -> listener.clienteNoValidado(record));
        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    void manejaPayloadNulo() {
        var recNull = new ConsumerRecord<String, IntegrationEventWrapper<ClienteNoValidadoEvent>>(
                "cliente.no-validado", 0, 1L, null, null);

        assertDoesNotThrow(() -> listener.clienteNoValidado(recNull));
        verifyNoInteractions(ordenValidacionService);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void capturaExcepcionDelServicio() {
        when(objectMapper.convertValue(any(), eq(ClienteNoValidadoEvent.class))).thenReturn(clienteFail);
        doThrow(new RuntimeException("svc")).when(ordenValidacionService)
                .registrarValidacionFallida(11L, ValidacionCrearOrden.CLIENTE);

        assertDoesNotThrow(() -> listener.clienteNoValidado(record));
        verify(ordenValidacionService).registrarValidacionFallida(11L, ValidacionCrearOrden.CLIENTE);
    }
}
