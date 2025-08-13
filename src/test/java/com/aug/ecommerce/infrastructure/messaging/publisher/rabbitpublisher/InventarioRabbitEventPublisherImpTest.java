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
 * Verifica que ambos métodos delegan en RabbitMQEventPublisher.
 */
@ExtendWith(MockitoExtension.class)
class InventarioRabbitEventPublisherImpTest {

    @Mock
    private ApplicationEventPublisher appPublisher; // requerido por el ctor

    @Mock
    private RabbitMQEventPublisher rabbitPublisher;

    @InjectMocks
    private InventarioRabbitEventPublisherImp publisher;

    @Test
    @DisplayName("publishStockDisponible delega en rabbitPublisher.publishEvent")
    void publishStockDisponible() {
        IntegrationEvent e = mock(IntegrationEvent.class);
        publisher.publishStockDisponible(e);
        verify(rabbitPublisher, times(1)).publishEvent(e);
    }

    @Test
    @DisplayName("publishStockNoDisponible delega en rabbitPublisher.publishEvent")
    void publishStockNoDisponible() {
        IntegrationEvent e = mock(IntegrationEvent.class);
        publisher.publishStockNoDisponible(e);
        verify(rabbitPublisher, times(1)).publishEvent(e);
    }
}
