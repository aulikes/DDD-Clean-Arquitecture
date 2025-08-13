package com.aug.ecommerce.infrastructure.messaging.publisher.eventpublisher;

import com.aug.ecommerce.application.events.ClienteNoValidadoEvent;
import com.aug.ecommerce.application.events.ClienteValidadoEvent;
import com.aug.ecommerce.application.events.IntegrationEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ClienteEventPublisherImp.
 */
@ExtendWith(MockitoExtension.class)
class ClienteEventPublisherImpTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private ClienteEventPublisherImp publisher;

    @Test
    @DisplayName("publishClienteValido: publica cuando ordenId != 5")
    void publishClienteValido_publica_siOrdenIdNoEs5() {
        var ev = new ClienteValidadoEvent(10L); // distinto de 5

        publisher.publishClienteValido(ev);

        verify(applicationEventPublisher, times(1)).publishEvent(ev);
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishClienteValido: NO publica cuando ordenId == 5")
    void publishClienteValido_noPublica_siOrdenIdEs5() {
        var ev = new ClienteValidadoEvent(5L); // caso especial: no debe publicar

        publisher.publishClienteValido(ev);

        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishClienteValido: ignora eventos de otro tipo")
    void publishClienteValido_ignora_tipoIncorrecto() {
        IntegrationEvent otro = mock(IntegrationEvent.class);

        publisher.publishClienteValido(otro);

        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishClienteNoValido: publica si el evento es ClienteNoValidadoEvent")
    void publishClienteNoValido_publica_tipoCorrecto() {
        var ev = new ClienteNoValidadoEvent(20L);

        publisher.publishClienteNoValido(ev);

        verify(applicationEventPublisher, times(1)).publishEvent(ev);
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishClienteNoValido: ignora eventos de otro tipo")
    void publishClienteNoValido_ignora_tipoIncorrecto() {
        IntegrationEvent otro = mock(IntegrationEvent.class);

        publisher.publishClienteNoValido(otro);

        verifyNoInteractions(applicationEventPublisher);
    }
}
