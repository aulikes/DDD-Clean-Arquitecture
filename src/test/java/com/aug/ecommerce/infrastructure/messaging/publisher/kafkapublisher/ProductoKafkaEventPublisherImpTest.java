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
 * Pruebas unitarias para ProductoKafkaEventPublisherImp.
 * Se validan publicaciones para producto creado, validado y no validado.
 */
@ExtendWith(MockitoExtension.class)
class ProductoKafkaEventPublisherImpTest {

    @Mock
    private KafkaEventPublisher<IntegrationEvent> kafkaPublisher;

    @Mock
    private AppProperties appProps;

    @Mock
    private AppProperties.Kafka kafkaProps;

    @Mock
    private AppProperties.Kafka.Producer producerProps;

    private ProductoKafkaEventPublisherImp publisher;

    @BeforeEach
    void setUp() {
        when(appProps.getKafka()).thenReturn(kafkaProps);
        when(kafkaProps.getProducer()).thenReturn(producerProps);

        publisher = new ProductoKafkaEventPublisherImp(kafkaPublisher, appProps);
    }

    @Test
    @DisplayName("Publica en producto.creado.topic")
    void publicarProductoCreado_casoExitoso() {
        IntegrationEvent evt = mock(IntegrationEvent.class);

        when(producerProps.getProductoCreadoTopic()).thenReturn("producto.creado.topic");
        publisher.publicarProductoCreado(evt);

        verify(kafkaPublisher, times(1)).publicar("producto.creado.topic", evt);
        verifyNoMoreInteractions(kafkaPublisher);
    }

    @Test
    @DisplayName("Publica en producto.validado.topic")
    void publishProductoValido_casoExitoso() {
        IntegrationEvent evt = mock(IntegrationEvent.class);

        when(producerProps.getProductoValidadoTopic()).thenReturn("producto.validado.topic");
        publisher.publishProductoValido(evt);

        verify(kafkaPublisher, times(1)).publicar("producto.validado.topic", evt);
        verifyNoMoreInteractions(kafkaPublisher);
    }

    @Test
    @DisplayName("Publica en producto.no-validado.topic")
    void publishProductoNoValido_casoExitoso() {
        IntegrationEvent evt = mock(IntegrationEvent.class);

        when(producerProps.getProductoNoValidadoTopic()).thenReturn("producto.no-validado.topic");
        publisher.publishProductoNoValido(evt);

        verify(kafkaPublisher, times(1)).publicar("producto.no-validado.topic", evt);
        verifyNoMoreInteractions(kafkaPublisher);
    }
}
