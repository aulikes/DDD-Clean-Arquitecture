package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.events.ClienteNoValidadoEvent;
import com.aug.ecommerce.application.events.ClienteValidadoEvent;
import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.publishers.ClienteEventPublisher;
import com.aug.ecommerce.domain.models.cliente.Cliente;
import com.aug.ecommerce.domain.repositories.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Estas pruebas unitarias validan el comportamiento de ClienteValidacionService:
 *  - Si el cliente existe: publica evento de cliente validado.
 *  - Si el cliente no existe: publica evento de cliente no validado.
 *
 * Se evita validar el orden; solo se comprueba que las interacciones y los argumentos sean correctos.
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
     * Debe publicar ClienteValidadoEvent cuando el cliente existe.
     */
    @Test
    void validarClienteCreacionOrden_publicaClienteValido_siClienteExiste() throws Exception {
        // Dado: el cliente existe en el repositorio
        Long ordenId = 99L;
        Long clienteId = 5L;
        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(new Cliente(clienteId, "John Doe", "john@doe.com")));

        // Cuando: se solicita la validación
        clienteValidacionService.validarClienteCreacionOrden(ordenId, clienteId);

        // Entonces: se consulta el repositorio con el ID correcto
        verify(clienteRepository).findById(clienteId);

        // Y: se publica un evento de cliente validado con el ordenId correcto
        verify(clienteEventPublisher).publishClienteValido(eventCaptor.capture());
        IntegrationEvent published = eventCaptor.getValue();
        assertThat(published).isInstanceOf(ClienteValidadoEvent.class);
        ClienteValidadoEvent evt = (ClienteValidadoEvent) published;
        assertThat(evt.ordenId()).isEqualTo(ordenId);

        // Y: no se publica el evento de “no validado”
        verify(clienteEventPublisher, never()).publishClienteNoValido(any());
    }

    /**
     * Debe publicar ClienteNoValidadoEvent cuando el cliente no existe.
     */
    @Test
    void validarClienteCreacionOrden_publicaClienteNoValido_siClienteNoExiste() throws Exception {
        // Dado: el cliente no existe
        Long ordenId = 88L;
        Long clienteId = 7L;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // Cuando: se solicita la validación
        clienteValidacionService.validarClienteCreacionOrden(ordenId, clienteId);

        // Entonces: se consulta el repositorio con el ID correcto
        verify(clienteRepository).findById(clienteId);

        // Y: se publica un evento de cliente NO validado con el ordenId correcto
        verify(clienteEventPublisher).publishClienteNoValido(eventCaptor.capture());
        IntegrationEvent published = eventCaptor.getValue();
        assertThat(published).isInstanceOf(ClienteNoValidadoEvent.class);
        ClienteNoValidadoEvent evt = (ClienteNoValidadoEvent) published;
        assertThat(evt.ordenId()).isEqualTo(ordenId);

        // Y: no se publica el evento de “validado”
        verify(clienteEventPublisher, never()).publishClienteValido(any());
    }
}
