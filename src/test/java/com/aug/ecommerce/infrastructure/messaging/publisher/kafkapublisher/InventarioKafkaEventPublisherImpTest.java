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
 * Pruebas unitarias para InventarioKafkaEventPublisherImp.
 * Se validan publicaciones para stock disponible y no disponible.
 */
@ExtendWith(MockitoExtension.class)
class InventarioKafkaEventPublisherImpTest {

    @Mock
    private KafkaEventPublisher<IntegrationEvent> kafkaPublisher;

    @Mock
    private AppProperties appProps;

    @Mock
    private AppProperties.Kafka kafkaProps;

    @Mock
    private AppProperties.Kafka.Producer producerProps;

    private InventarioKafkaEventPublisherImp publisher;

    @BeforeEach
    void setUp() {
        when(appProps.getKafka()).thenReturn(kafkaProps);
        when(kafkaProps.getProducer()).thenReturn(producerProps);

        publisher = new InventarioKafkaEventPublisherImp(kafkaPublisher, appProps);
    }

    @Test
    @DisplayName("Publica en inventario.validado.topic (stock disponible)")
    void publishStockDisponible_casoExitoso() {
        IntegrationEvent evt = mock(IntegrationEvent.class);

        when(producerProps.getInventarioValidadoTopic()).thenReturn("inventario.validado.topic");
        publisher.publishStockDisponible(evt);

        verify(kafkaPublisher, times(1)).publicar("inventario.validado.topic", evt);
        verifyNoMoreInteractions(kafkaPublisher);
    }

    @Test
    @DisplayName("Publica en inventario.no-validado.topic (stock no disponible)")
    void publishStockNoDisponible_casoExitoso() {
        IntegrationEvent evt = mock(IntegrationEvent.class);

        when(producerProps.getInventarioNoValidadoTopic()).thenReturn("inventario.no-validado.topic");
        publisher.publishStockNoDisponible(evt);

        verify(kafkaPublisher, times(1)).publicar("inventario.no-validado.topic", evt);
        verifyNoMoreInteractions(kafkaPublisher);
    }
}
