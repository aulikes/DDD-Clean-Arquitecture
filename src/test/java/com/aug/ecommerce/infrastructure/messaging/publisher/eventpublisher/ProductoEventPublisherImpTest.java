package com.aug.ecommerce.infrastructure.messaging.publisher.eventpublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.events.ProductoCreadoEvent;
import com.aug.ecommerce.application.events.ProductoNoValidadoEvent;
import com.aug.ecommerce.application.events.ProductoValidadoEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ProductoEventPublisherImp.
 */
@ExtendWith(MockitoExtension.class)
class ProductoEventPublisherImpTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private ProductoEventPublisherImp publisher;

    @Test
    @DisplayName("publicarProductoCreado publica el ProductoCreadoEvent recibido")
    void publicarProductoCreado_publica() {
        var ev = new ProductoCreadoEvent(555L, 99L);

        publisher.publicarProductoCreado(ev);

        verify(applicationEventPublisher, times(1)).publishEvent(ev);
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publicarProductoCreado ignora eventos de otro tipo")
    void publicarProductoCreado_ignoraOtroTipo() {
        IntegrationEvent otro = mock(IntegrationEvent.class);

        // Llamamos al método con un evento que NO es ProductoCreadoEvent.
        publisher.publicarProductoCreado(otro);

        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishProductoValido publica solo si es ProductoValidadoEvent")
    void publishProductoValido_publicaCuandoEsProductoValidado() {
        var ev = new ProductoValidadoEvent(777L);

        publisher.publishProductoValido(ev);

        verify(applicationEventPublisher, times(1)).publishEvent(ev);
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishProductoValido no publica si NO es ProductoValidadoEvent")
    void publishProductoValido_ignoraOtroTipo() {
        IntegrationEvent otro = mock(IntegrationEvent.class);

        publisher.publishProductoValido(otro);

        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishProductoNoValido publica solo si es ProductoNoValidadoEvent")
    void publishProductoNoValido_publicaCuandoEsProductoNoValidado() {
        var ev = new ProductoNoValidadoEvent(888L);

        publisher.publishProductoNoValido(ev);

        verify(applicationEventPublisher, times(1)).publishEvent(ev);
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishProductoNoValido no publica si NO es ProductoNoValidadoEvent")
    void publishProductoNoValido_ignoraOtroTipo() {
        IntegrationEvent otro = mock(IntegrationEvent.class);

        publisher.publishProductoNoValido(otro);

        verifyNoInteractions(applicationEventPublisher);
    }
}
