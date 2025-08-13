package com.aug.ecommerce.infrastructure.messaging.publisher.eventpublisher;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.events.OrdenPagadaEvent;
import com.aug.ecommerce.application.events.OrdenPreparadaParaPagoEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Tests unitarios para OrdenEventPublisherImp.
 */
@ExtendWith(MockitoExtension.class)
class OrdenEventPublisherImpTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private OrdenEventPublisherImp publisher;

    // ---------- publishOrdenCreated ----------

    @Test
    @DisplayName("publishOrdenCreated: publica cuando es OrdenCreadaEvent")
    void publishOrdenCreated_publica_tipoCorrecto() {
        var ev = new OrdenCreadaEvent(
                1001L,
                2002L,
                "CL 1 # 2-3",
                List.of(new OrdenCreadaEvent.ItemOrdenCreada(10L, 3))
        );

        publisher.publishOrdenCreated(ev);

        verify(applicationEventPublisher, times(1)).publishEvent(ev);
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishOrdenCreated: no publica cuando NO es OrdenCreadaEvent")
    void publishOrdenCreated_ignora_otroTipo() {
        IntegrationEvent otro = mock(IntegrationEvent.class);

        publisher.publishOrdenCreated(otro);

        verifyNoInteractions(applicationEventPublisher);
    }

    // ---------- publishOrdenPagoRequerido ----------

    @Test
    @DisplayName("publishOrdenPagoRequerido: publica cuando es OrdenPreparadaParaPagoEvent")
    void publishOrdenPagoRequerido_publica_tipoCorrecto() {
        var ev = new OrdenPreparadaParaPagoEvent(1001L, 123.45, "TARJETA");

        publisher.publishOrdenPagoRequerido(ev);

        verify(applicationEventPublisher, times(1)).publishEvent(ev);
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishOrdenPagoRequerido: no publica cuando NO es OrdenPreparadaParaPagoEvent")
    void publishOrdenPagoRequerido_ignora_otroTipo() {
        IntegrationEvent otro = mock(IntegrationEvent.class);

        publisher.publishOrdenPagoRequerido(otro);

        verifyNoInteractions(applicationEventPublisher);
    }

    // ---------- publishOrdenEnvioRequerido ----------

    @Test
    @DisplayName("publishOrdenEnvioRequerido: publica cuando es OrdenPagadaEvent")
    void publishOrdenEnvioRequerido_publica_tipoCorrecto() {
        var ev = new OrdenPagadaEvent(1001L, "CL 1 # 2-3");

        publisher.publishOrdenEnvioRequerido(ev);

        verify(applicationEventPublisher, times(1)).publishEvent(ev);
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    @DisplayName("publishOrdenEnvioRequerido: no publica cuando NO es OrdenPagadaEvent")
    void publishOrdenEnvioRequerido_ignora_otroTipo() {
        IntegrationEvent otro = mock(IntegrationEvent.class);

        publisher.publishOrdenEnvioRequerido(otro);

        verifyNoInteractions(applicationEventPublisher);
    }
}
