package com.aug.ecommerce.infrastructure.messaging.publisher.rabbitpublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.infrastructure.messaging.RabbitMQEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Verifica que publicarEnvioPreparado delega en RabbitMQEventPublisher.
 */
@ExtendWith(MockitoExtension.class)
class EnvioRabbitEventPublisherImpTest {

    @Mock
    private RabbitMQEventPublisher rabbitPublisher;

    @InjectMocks
    private EnvioRabbitEventPublisherImp publisher;

    @Test
    @DisplayName("publicarEnvioPreparado delega en rabbitPublisher.publishEvent")
    void publicarEnvioPreparado() {
        IntegrationEvent e = mock(IntegrationEvent.class);
        publisher.publicarEnvioPreparado(e);

        verify(rabbitPublisher, times(1)).publishEvent(e);
        verifyNoMoreInteractions(rabbitPublisher);
    }
}
