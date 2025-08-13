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
 * Pruebas unitarias para EnvioKafkaEventPublisherImp.
 * Se valida que se publique en el topic configurado para envíos preparados.
 */
@ExtendWith(MockitoExtension.class)
class EnvioKafkaEventPublisherImpTest {

    @Mock
    private KafkaEventPublisher<IntegrationEvent> kafkaPublisher;

    @Mock
    private AppProperties appProps;

    @Mock
    private AppProperties.Kafka kafkaProps;

    @Mock
    private AppProperties.Kafka.Producer producerProps;

    private EnvioKafkaEventPublisherImp publisher;

    @BeforeEach
    void setUp() {
        when(appProps.getKafka()).thenReturn(kafkaProps);
        when(kafkaProps.getProducer()).thenReturn(producerProps);

        publisher = new EnvioKafkaEventPublisherImp(kafkaPublisher, appProps);
    }

    @Test
    @DisplayName("Publica en envio.preparado.topic")
    void publicarEnvioPreparado_casoExitoso() {
        IntegrationEvent evt = mock(IntegrationEvent.class);

        when(producerProps.getEnvioPreparadoTopic()).thenReturn("envio.preparado.topic");
        publisher.publicarEnvioPreparado(evt);

        verify(kafkaPublisher, times(1)).publicar("envio.preparado.topic", evt);
        verifyNoMoreInteractions(kafkaPublisher);
    }
}
