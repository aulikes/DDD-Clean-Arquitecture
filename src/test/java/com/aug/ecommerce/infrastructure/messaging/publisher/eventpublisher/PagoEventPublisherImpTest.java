package com.aug.ecommerce.infrastructure.messaging.publisher.eventpublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.events.PagoConfirmadoEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;

import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PagoEventPublisherImp.
 */
@ExtendWith(MockitoExtension.class)
class PagoEventPublisherImpTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private PagoEventPublisherImp publisher;

    @Test
    @DisplayName("publicarPagoRealizado: publica cuando el evento es PagoConfirmadoEvent")
    void publicarPagoRealizado_publica_pagoConfirmado() {
        // Arrange: evento válido para este publisher
        var ev = new PagoConfirmadoEvent(
                1001L,          // ordenId
                9001L,          // pagoId
                Instant.now(),  // fecha
                true,           // exitoso
                "TRX-123",      // codigoTransaccion
                null            // mensajeError
        );

        // Act
        publisher.publicarPagoRealizado(ev);

        // Assert: debe delegar en ApplicationEventPublisher
        verify(applicationEventPublisher, times(1)).publishEvent(ev);
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publicarPagoRealizado: no publica cuando NO es PagoConfirmadoEvent")
    void publicarPagoRealizado_ignora_otroTipo() {
        // Arrange: cualquier otro IntegrationEvent
        IntegrationEvent otro = mock(IntegrationEvent.class);

        // Act
        publisher.publicarPagoRealizado(otro);

        // Assert: no debe publicar nada
        verifyNoInteractions(applicationEventPublisher);
    }
}
