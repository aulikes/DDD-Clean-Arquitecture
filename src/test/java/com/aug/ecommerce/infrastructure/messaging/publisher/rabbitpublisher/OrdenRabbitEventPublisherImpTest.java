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
 * Verifica que las 3 publicaciones delegan en RabbitMQEventPublisher.
 */
@ExtendWith(MockitoExtension.class)
class OrdenRabbitEventPublisherImpTest {

    @Mock
    private RabbitMQEventPublisher rabbitPublisher;

    @InjectMocks
    private OrdenRabbitEventPublisherImp publisher;

    @Test
    @DisplayName("publishOrdenCreated delega en rabbitPublisher.publishEvent")
    void publishOrdenCreated() {
        IntegrationEvent e = mock(IntegrationEvent.class);
        publisher.publishOrdenCreated(e);
        verify(rabbitPublisher, times(1)).publishEvent(e);
    }

    @Test
    @DisplayName("publishOrdenPagoRequerido delega en rabbitPublisher.publishEvent")
    void publishOrdenPagoRequerido() {
        IntegrationEvent e = mock(IntegrationEvent.class);
        publisher.publishOrdenPagoRequerido(e);
        verify(rabbitPublisher, times(1)).publishEvent(e);
    }

    @Test
    @DisplayName("publishOrdenEnvioRequerido delega en rabbitPublisher.publishEvent")
    void publishOrdenEnvioRequerido() {
        IntegrationEvent e = mock(IntegrationEvent.class);
        publisher.publishOrdenEnvioRequerido(e);
        verify(rabbitPublisher, times(1)).publishEvent(e);
    }
}
