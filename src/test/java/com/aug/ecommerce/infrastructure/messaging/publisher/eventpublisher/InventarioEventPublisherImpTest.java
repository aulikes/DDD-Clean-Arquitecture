package com.aug.ecommerce.infrastructure.messaging.publisher.eventpublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.events.InventarioNoValidadoEvent;
import com.aug.ecommerce.application.events.InventarioValidadoEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.*;

/**
 * Tests unitarios para InventarioEventPublisherImp.
 */
@ExtendWith(MockitoExtension.class)
class InventarioEventPublisherImpTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private InventarioEventPublisherImp publisher;

    @Test
    @DisplayName("publishStockDisponible: publica cuando es InventarioValidadoEvent")
    void publishStockDisponible_publica_tipoCorrecto() {
        var ev = new InventarioValidadoEvent(1001L);

        publisher.publishStockDisponible(ev);

        verify(applicationEventPublisher, times(1)).publishEvent(ev);
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishStockDisponible: no publica cuando NO es InventarioValidadoEvent")
    void publishStockDisponible_ignora_otroTipo() {
        IntegrationEvent otro = mock(IntegrationEvent.class);

        publisher.publishStockDisponible(otro);

        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishStockNoDisponible: publica cuando es InventarioNoValidadoEvent")
    void publishStockNoDisponible_publica_tipoCorrecto() {
        var ev = new InventarioNoValidadoEvent(2002L);

        publisher.publishStockNoDisponible(ev);

        verify(applicationEventPublisher, times(1)).publishEvent(ev);
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishStockNoDisponible: no publica cuando NO es InventarioNoValidadoEvent")
    void publishStockNoDisponible_ignora_otroTipo() {
        IntegrationEvent otro = mock(IntegrationEvent.class);

        publisher.publishStockNoDisponible(otro);

        verifyNoInteractions(applicationEventPublisher);
    }
}
