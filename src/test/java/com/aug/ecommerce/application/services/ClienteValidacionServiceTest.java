package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.events.ClienteNoValidadoEvent;
import com.aug.ecommerce.application.events.ClienteValidadoEvent;
import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.publishers.ClienteEventPublisher;
import com.aug.ecommerce.domain.models.cliente.Cliente;
import com.aug.ecommerce.domain.repositories.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Estas pruebas unitarias validan el comportamiento de ClienteValidacionService:
 *  - validarClienteCreacionOrden(): publica 'cliente validado' si el cliente existe.
 *  - validarClienteCreacionOrden(): publica 'cliente no validado' si el cliente no existe o hay error.
 *
 * Se usa inyección de mocks (@InjectMocks) y se verifica el orden de interacción:
 * repositorio -> publisher. Se captura el IntegrationEvent publicado y se validan sus campos.
 */
@ExtendWith(MockitoExtension.class)
class ClienteValidacionServiceTest {

    @Mock
    ClienteRepository clienteRepository;

    @Mock
    ClienteEventPublisher clienteEventPublisher;

    @InjectMocks
    ClienteValidacionService clienteValidacionService;

    @Captor
    ArgumentCaptor<IntegrationEvent> eventCaptor;

    /**
     * Debe publicar el evento de validación exitosa cuando el cliente existe.
     * Orden esperado: buscar en repo -> publicar evento válido.
     */
    @Test
    void validarClienteCreacionOrden_debePublicarClienteValido_siExiste() throws Exception {
        // Dado
        Long ordenId = 99L;
        Long clienteId = 5L;
        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(new Cliente(clienteId, "John Doe", "john@doe.com")));

        // Cuando
        clienteValidacionService.validarClienteCreacionOrden(ordenId, clienteId);

        // Entonces: orden de llamadas
        InOrder inOrder = inOrder(clienteRepository, clienteEventPublisher);
        inOrder.verify(clienteRepository).findById(clienteId);
        inOrder.verify(clienteEventPublisher).publishClienteValido(eventCaptor.capture());
        inOrder.verifyNoMoreInteractions();

        // Y: contenido del evento
        IntegrationEvent published = eventCaptor.getValue();
        assertThat(published).isInstanceOf(ClienteValidadoEvent.class);
        ClienteValidadoEvent evt = (ClienteValidadoEvent) published;
        assertThat(evt.ordenId()).isEqualTo(99L);

        // Y: no se publica el evento de no validación
        verify(clienteEventPublisher, never()).publishClienteNoValido(any());
    }

    /**
     * Debe publicar el evento de no validación cuando el cliente no existe.
     * Orden esperado: buscar en repo -> publicar evento no válido.
     */
    @Test
    void validarClienteCreacionOrden_debePublicarClienteNoValido_siNoExiste() throws Exception {
        // Dado
        Long ordenId = 88L;
        Long clienteId = 7L;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // Cuando
        clienteValidacionService.validarClienteCreacionOrden(ordenId, clienteId);

        // Entonces: orden de llamadas
        InOrder inOrder = inOrder(clienteRepository, clienteEventPublisher);
        inOrder.verify(clienteRepository).findById(clienteId);
        inOrder.verify(clienteEventPublisher).publishClienteNoValido(eventCaptor.capture());
        inOrder.verifyNoMoreInteractions();

        // Y: contenido del evento
        IntegrationEvent published = eventCaptor.getValue();
        assertThat(published).isInstanceOf(ClienteNoValidadoEvent.class);
        ClienteNoValidadoEvent evt = (ClienteNoValidadoEvent) published;
        assertThat(evt.ordenId()).isEqualTo(88L);

        // Y: no se publica el evento de validación
        verify(clienteEventPublisher, never()).publishClienteValido(any());
    }
}
