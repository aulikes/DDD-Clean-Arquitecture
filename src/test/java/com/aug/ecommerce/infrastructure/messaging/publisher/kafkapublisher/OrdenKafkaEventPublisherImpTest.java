package com.aug.ecommerce.infrastructure.messaging.publisher.kafkapublisher;

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
 * Pruebas unitarias para OrdenKafkaEventPublisherImp.
 * Se valida la publicación en los topics de creación, pago requerido y envío requerido.
 */
@ExtendWith(MockitoExtension.class)
class OrdenKafkaEventPublisherImpTest {

    @Mock
    private KafkaEventPublisher<IntegrationEvent> kafkaPublisher;

    @Mock
    private AppProperties appProps;

    @Mock
    private AppProperties.Kafka kafkaProps;

    @Mock
    private AppProperties.Kafka.Producer producerProps;

    private OrdenKafkaEventPublisherImp publisher;

    @BeforeEach
    void setUp() {
        when(appProps.getKafka()).thenReturn(kafkaProps);
        when(kafkaProps.getProducer()).thenReturn(producerProps);

        publisher = new OrdenKafkaEventPublisherImp(kafkaPublisher, appProps);
    }

    @Test
    @DisplayName("Publica en orden.creada.topic")
    void publishOrdenCreated_casoExitoso() {
        IntegrationEvent evt = mock(IntegrationEvent.class);

        when(producerProps.getOrdenCreadaTopic()).thenReturn("orden.creada.topic");
        publisher.publishOrdenCreated(evt);

        verify(kafkaPublisher, times(1)).publicar("orden.creada.topic", evt);
        verifyNoMoreInteractions(kafkaPublisher);
    }

    @Test
    @DisplayName("Publica en orden.preparada.pago.topic")
    void publishOrdenPagoRequerido_casoExitoso() {
        IntegrationEvent evt = mock(IntegrationEvent.class);

        when(producerProps.getOrdenPreparadaPagoTopic()).thenReturn("orden.preparada.pago.topic");
        publisher.publishOrdenPagoRequerido(evt);

        verify(kafkaPublisher, times(1)).publicar("orden.preparada.pago.topic", evt);
        verifyNoMoreInteractions(kafkaPublisher);
    }

    @Test
    @DisplayName("Publica en orden.pagada.topic")
    void publishOrdenEnvioRequerido_casoExitoso() {
        IntegrationEvent evt = mock(IntegrationEvent.class);

        when(producerProps.getOrdenPagadaTopic()).thenReturn("orden.pagada.topic");
        publisher.publishOrdenEnvioRequerido(evt);

        verify(kafkaPublisher, times(1)).publicar("orden.pagada.topic", evt);
        verifyNoMoreInteractions(kafkaPublisher);
    }
}
