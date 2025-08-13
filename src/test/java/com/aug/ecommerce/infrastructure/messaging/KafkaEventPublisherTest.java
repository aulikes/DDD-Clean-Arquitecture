package com.aug.ecommerce.infrastructure.messaging;

import com.aug.ecommerce.application.events.ClienteValidadoEvent;
import com.aug.ecommerce.application.events.IntegrationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para KafkaEventPublisher.
 * Valida que construye IntegrationEventWrapper correctamente y delega en KafkaTemplate.
 */
@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherTest {

    @Mock
    private KafkaTemplate<String, IntegrationEventWrapper<IntegrationEvent>> kafkaTemplate;

    @Mock
    private EventTypeResolver eventTypeResolver;

    private KafkaEventPublisher<IntegrationEvent> publisher;

    @BeforeEach
    void setUp() {
        publisher = new KafkaEventPublisher<>(kafkaTemplate, eventTypeResolver);
    }

    @Test
    @DisplayName("publicar: envía wrapper; la key es no nula y el wrapper tiene metadatos correctos")
    void publicar_enviaWrapperConDatosCorrectos() {
        IntegrationEvent evt = new ClienteValidadoEvent(42L);
        when(eventTypeResolver.resolveEventType(evt)).thenReturn("cliente.orden.valido");

        publisher.publicar("cliente.validado.topic", evt);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<IntegrationEventWrapper> wrapperCaptor = ArgumentCaptor.forClass(IntegrationEventWrapper.class);

        verify(kafkaTemplate, times(1))
                .send(eq("cliente.validado.topic"), keyCaptor.capture(), wrapperCaptor.capture());
        verifyNoMoreInteractions(kafkaTemplate);

        String usedKey = keyCaptor.getValue();
        assertNotNull(usedKey);
        assertFalse(usedKey.isBlank(), "La key usada en Kafka no debe ser vacía");

        IntegrationEventWrapper<IntegrationEvent> sent = wrapperCaptor.getValue();
        assertEquals("cliente.orden.valido", sent.eventType());
        assertEquals(evt.getVersion(), sent.version());
        assertNotNull(sent.traceId(), "El wrapper debe tener traceId");
        assertNotNull(sent.timestamp(), "El wrapper debe tener timestamp");
        assertSame(evt, sent.data(), "El payload del wrapper debe ser el mismo evento");
    }

    @Test
    @DisplayName("publicar: captura excepción del template sin propagarla")
    void publicar_capturaExcepcionDelTemplate() {
        IntegrationEvent evt = new ClienteValidadoEvent(100L);
        when(eventTypeResolver.resolveEventType(evt)).thenReturn("cliente.orden.valido");

        doThrow(new RuntimeException("send failed"))
                .when(kafkaTemplate)
                .send(eq("cliente.validado.topic"), anyString(), any(IntegrationEventWrapper.class));

        // No debe lanzar excepción
        publisher.publicar("cliente.validado.topic", evt);

        verify(kafkaTemplate, times(1))
                .send(eq("cliente.validado.topic"), anyString(), any(IntegrationEventWrapper.class));
        verifyNoMoreInteractions(kafkaTemplate);
    }
}
