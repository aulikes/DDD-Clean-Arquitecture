package com.aug.ecommerce.infrastructure.messaging.publisher.rabbitpublisher;

import com.aug.ecommerce.application.events.ClienteValidadoEvent;
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
 * Verifica la lógica especial:
 * - publishClienteValido SOLO publica si ordenId != 5.
 * - publishClienteNoValido siempre publica.
 */
@ExtendWith(MockitoExtension.class)
class ClienteRabbitEventPublisherImpTest {

    @Mock
    private RabbitMQEventPublisher rabbitPublisher;

    @InjectMocks
    private ClienteRabbitEventPublisherImp publisher;

    @Test
    @DisplayName("publishClienteValido: publica cuando ordenId != 5")
    void publishClienteValido_publicaCuandoOrdenIdNoEs5() {
        var event = new ClienteValidadoEvent(10L); // distinto de 5
        publisher.publishClienteValido(event);

        verify(rabbitPublisher, times(1)).publishEvent(event);
        verifyNoMoreInteractions(rabbitPublisher);
    }

    @Test
    @DisplayName("publishClienteNoValido: siempre publica")
    void publishClienteNoValido_siemprePublica() {
        IntegrationEvent anyEvent = mock(IntegrationEvent.class);
        publisher.publishClienteNoValido(anyEvent);

        verify(rabbitPublisher, times(1)).publishEvent(anyEvent);
        verifyNoMoreInteractions(rabbitPublisher);
    }
}
