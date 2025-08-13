package com.aug.ecommerce.infrastructure.messaging.publisher.kafkapublisher;

import com.aug.ecommerce.application.events.ClienteValidadoEvent;
import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.infrastructure.config.AppProperties;
import com.aug.ecommerce.infrastructure.messaging.KafkaEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ClienteKafkaEventPublisherImp.
 * Se valida la publicación en topics correctos y que siempre publique para ClienteValidadoEvent.
 */
@ExtendWith(MockitoExtension.class)
class ClienteKafkaEventPublisherImpTest {

    @Mock
    private KafkaEventPublisher<IntegrationEvent> kafkaPublisher;

    @Mock
    private AppProperties appProps;

    @Mock
    private AppProperties.Kafka kafkaProps;

    @Mock
    private AppProperties.Kafka.Producer producerProps;

    private ClienteKafkaEventPublisherImp publisher;

    @BeforeEach
    void setUp() {
        // Se configuran las propiedades antes de crear el SUT para evitar NPE en el constructor.
        when(appProps.getKafka()).thenReturn(kafkaProps);
        when(kafkaProps.getProducer()).thenReturn(producerProps);

        publisher = new ClienteKafkaEventPublisherImp(kafkaPublisher, appProps);
    }

    @Test
    @DisplayName("publishClienteValido publica en cliente.validado.topic para cualquier ordenId")
    void publishClienteValido_publicaSiempre() {
        when(producerProps.getClienteValidadoTopic()).thenReturn("cliente.validado.topic");
        // Se prueban dos casos: ordenId 10 y ordenId 5 (ya no hay regla especial).
        IntegrationEvent evt1 = new ClienteValidadoEvent(10L);
        IntegrationEvent evt2 = new ClienteValidadoEvent(5L);

        publisher.publishClienteValido(evt1);
        publisher.publishClienteValido(evt2);

        verify(kafkaPublisher, times(1)).publicar("cliente.validado.topic", evt1);
        verify(kafkaPublisher, times(1)).publicar("cliente.validado.topic", evt2);
        verifyNoMoreInteractions(kafkaPublisher);
    }

    @Test
    @DisplayName("publishClienteValido ignora eventos que no son ClienteValidadoEvent")
    void publishClienteValido_ignoraOtrosEventos() {
        IntegrationEvent otro = mock(IntegrationEvent.class);

        publisher.publishClienteValido(otro);

        verifyNoInteractions(kafkaPublisher);
    }

    @Test
    @DisplayName("publishClienteNoValido publica siempre en cliente.no-validado.topic")
    void publishClienteNoValido_publicaSiempre() {
        IntegrationEvent evt = new ClienteValidadoEvent(123L);

        when(producerProps.getClienteNoValidadoTopic()).thenReturn("cliente.no-validado.topic");
        publisher.publishClienteNoValido(evt);

        verify(kafkaPublisher, times(1)).publicar("cliente.no-validado.topic", evt);
        verifyNoMoreInteractions(kafkaPublisher);
    }
}
