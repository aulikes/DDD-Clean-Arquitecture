package com.aug.ecommerce.infrastructure.messaging.publisher.rabbitpublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.infrastructure.messaging.RabbitMQEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.*;

/**
 * Verifica que publicarPagoRealizado delega en RabbitMQEventPublisher.
 */
@ExtendWith(MockitoExtension.class)
class PagoRabbitEventPublisherImpTest {

    @Mock
    private ApplicationEventPublisher appPublisher; // requerido por el ctor

    @Mock
    private RabbitMQEventPublisher rabbitPublisher;

    @InjectMocks
    private PagoRabbitEventPublisherImp publisher;

    @Test
    @DisplayName("publicarPagoRealizado delega en rabbitPublisher.publishEvent")
    void publicarPagoRealizado() {
        IntegrationEvent e = mock(IntegrationEvent.class);
        publisher.publicarPagoRealizado(e);

        verify(rabbitPublisher, times(1)).publishEvent(e);
        verifyNoMoreInteractions(rabbitPublisher);
    }
}
