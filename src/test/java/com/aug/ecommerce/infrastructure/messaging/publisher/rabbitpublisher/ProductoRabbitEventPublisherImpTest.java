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
 * Verifica que delega SIEMPRE en RabbitMQEventPublisher
 * para: publicarProductoCreado, publishProductoValido, publishProductoNoValido.
 */
@ExtendWith(MockitoExtension.class)
class ProductoRabbitEventPublisherImpTest {

    @Mock
    private ApplicationEventPublisher appPublisher; // está en el constructor, aunque no se use

    @Mock
    private RabbitMQEventPublisher rabbitPublisher;

    @InjectMocks
    private ProductoRabbitEventPublisherImp publisher;

    @Test
    @DisplayName("publicarProductoCreado delega en rabbitPublisher.publishEvent")
    void publicarProductoCreado() {
        IntegrationEvent e = mock(IntegrationEvent.class);
        publisher.publicarProductoCreado(e);
        verify(rabbitPublisher, times(1)).publishEvent(e);
    }

    @Test
    @DisplayName("publishProductoValido delega en rabbitPublisher.publishEvent")
    void publishProductoValido() {
        IntegrationEvent e = mock(IntegrationEvent.class);
        publisher.publishProductoValido(e);
        verify(rabbitPublisher, times(1)).publishEvent(e);
    }

    @Test
    @DisplayName("publishProductoNoValido delega en rabbitPublisher.publishEvent")
    void publishProductoNoValido() {
        IntegrationEvent e = mock(IntegrationEvent.class);
        publisher.publishProductoNoValido(e);
        verify(rabbitPublisher, times(1)).publishEvent(e);
    }
}
