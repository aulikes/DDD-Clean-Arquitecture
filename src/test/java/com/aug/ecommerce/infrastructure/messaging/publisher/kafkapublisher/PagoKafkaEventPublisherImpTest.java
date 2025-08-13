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
 * Pruebas unitarias para PagoKafkaEventPublisherImp.
 * Se valida la publicación en el topic configurado de pago realizado.
 */
@ExtendWith(MockitoExtension.class)
class PagoKafkaEventPublisherImpTest {

    @Mock
    private KafkaEventPublisher<IntegrationEvent> kafkaPublisher;

    @Mock
    private AppProperties appProps;

    @Mock
    private AppProperties.Kafka kafkaProps;

    @Mock
    private AppProperties.Kafka.Producer producerProps;

    private PagoKafkaEventPublisherImp publisher;

    @BeforeEach
    void setUp() {
        when(appProps.getKafka()).thenReturn(kafkaProps);
        when(kafkaProps.getProducer()).thenReturn(producerProps);

        publisher = new PagoKafkaEventPublisherImp(kafkaPublisher, appProps);
    }

    @Test
    @DisplayName("Publica en pago.realizado.topic")
    void publicarPagoRealizado_casoExitoso() {
        IntegrationEvent evt = mock(IntegrationEvent.class);

        when(producerProps.getPagoRealizadoTopic()).thenReturn("pago.realizado.topic");
        publisher.publicarPagoRealizado(evt);

        verify(kafkaPublisher, times(1)).publicar("pago.realizado.topic", evt);
        verifyNoMoreInteractions(kafkaPublisher);
    }
}
