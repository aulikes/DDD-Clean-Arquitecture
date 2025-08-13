package com.aug.ecommerce.infrastructure.messaging.publisher.eventpublisher;

import com.aug.ecommerce.application.events.EnvioPreparadoEvent;
import com.aug.ecommerce.application.events.IntegrationEvent;
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
 * Tests unitarios para EnvioEventPublisherImp.
 */
@ExtendWith(MockitoExtension.class)
class EnvioEventPublisherImpTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private EnvioEventPublisherImp publisher;

    @Test
    @DisplayName("publicarEnvioPreparado: publica cuando el evento es EnvioPreparadoEvent")
    void publicarEnvioPreparado_publica_envioPreparado() {
        var ev = new EnvioPreparadoEvent(
                1001L,           // ordenId
                5001L,           // envioId
                Instant.now(),   // timestamp
                true,            // exito
                "TRK-123",       // trackingId
                null             // razonFallo
        );

        publisher.publicarEnvioPreparado(ev);

        verify(applicationEventPublisher, times(1)).publishEvent(ev);
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publicarEnvioPreparado: no publica cuando el evento NO es EnvioPreparadoEvent")
    void publicarEnvioPreparado_ignora_otroTipo() {
        IntegrationEvent otro = mock(IntegrationEvent.class);

        publisher.publicarEnvioPreparado(otro);

        verifyNoInteractions(applicationEventPublisher);
    }
}
