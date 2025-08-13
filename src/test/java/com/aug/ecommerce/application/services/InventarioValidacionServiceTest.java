package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.events.InventarioNoValidadoEvent;
import com.aug.ecommerce.application.events.InventarioValidadoEvent;
import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.publishers.InventarioEventPublisher;
import com.aug.ecommerce.domain.models.inventario.Inventario;
import com.aug.ecommerce.domain.repositories.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para InventarioValidacionService
 */
@ExtendWith(MockitoExtension.class)
class InventarioValidacionServiceTest {

    @Mock
    InventarioRepository inventarioRepository;

    @Mock
    InventarioEventPublisher inventarioEventPublisher;

    @InjectMocks
    InventarioValidacionService inventarioValidacionService;

    @Captor
    ArgumentCaptor<IntegrationEvent> eventCaptor;

    /**
     * Caso feliz: todos los productos tienen stock suficiente.
     * Debe publicar InventarioValidadoEvent con el ordenId correcto y no publicar NoValidado.
     */
    @Test
    void validarInventarioCreacionOrden_publicaInventarioValidado_siTodosConStock() throws Exception {
        Long ordenId = 99L;
        var items = List.of(
                new OrdenCreadaEvent.ItemOrdenCreada(1L, 3),
                new OrdenCreadaEvent.ItemOrdenCreada(2L, 4)
        );

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(new Inventario(1L, 10L)));
        when(inventarioRepository.findById(2L)).thenReturn(Optional.of(new Inventario(2L, 20L)));

        inventarioValidacionService.validarInventarioCreacionOrden(ordenId, items);

        // Consultas a repositorio
        verify(inventarioRepository).findById(1L);
        verify(inventarioRepository).findById(2L);

        // Publicación de evento OK
        verify(inventarioEventPublisher).publishStockDisponible(eventCaptor.capture());
        IntegrationEvent published = eventCaptor.getValue();
        assertThat(published).isInstanceOf(InventarioValidadoEvent.class);
        assertThat(((InventarioValidadoEvent) published).ordenId()).isEqualTo(ordenId);

        // No debe publicar el evento de no disponibilidad
        verify(inventarioEventPublisher, never()).publishStockNoDisponible(any());
    }

    /**
     * Caso: uno de los productos no tiene stock suficiente.
     * Debe publicar InventarioNoValidadoEvent con el ordenId correcto.
     */
    @Test
    void validarInventarioCreacionOrden_publicaInventarioNoValidado_siFaltaStock() throws Exception {
        Long ordenId = 77L;
        var items = List.of(
                new OrdenCreadaEvent.ItemOrdenCreada(10L, 5),   // requiere 5
                new OrdenCreadaEvent.ItemOrdenCreada(11L, 1)    // suficiente
        );

        when(inventarioRepository.findById(10L)).thenReturn(Optional.of(new Inventario(10L, 1L))); 

        inventarioValidacionService.validarInventarioCreacionOrden(ordenId, items);

        // Publicación de evento de NO disponibilidad
        verify(inventarioEventPublisher).publishStockNoDisponible(eventCaptor.capture());
        IntegrationEvent published = eventCaptor.getValue();
        assertThat(published).isInstanceOf(InventarioNoValidadoEvent.class);
        assertThat(((InventarioNoValidadoEvent) published).ordenId()).isEqualTo(ordenId);

        // No debe publicar el evento de disponibilidad
        verify(inventarioEventPublisher, never()).publishStockDisponible(any());
    }

    /**
     * Caso: el inventario de un producto no existe (Optional.empty()).
     * Debe tratarse como no disponible y publicar InventarioNoValidadoEvent.
     */
    @Test
    void validarInventarioCreacionOrden_publicaInventarioNoValidado_siNoExisteInventario() throws Exception {
        Long ordenId = 55L;
        var items = List.of(new OrdenCreadaEvent.ItemOrdenCreada(100L, 1));

        when(inventarioRepository.findById(100L)).thenReturn(Optional.empty());

        inventarioValidacionService.validarInventarioCreacionOrden(ordenId, items);

        verify(inventarioEventPublisher).publishStockNoDisponible(eventCaptor.capture());
        IntegrationEvent published = eventCaptor.getValue();
        assertThat(published).isInstanceOf(InventarioNoValidadoEvent.class);
        assertThat(((InventarioNoValidadoEvent) published).ordenId()).isEqualTo(ordenId);

        verify(inventarioEventPublisher, never()).publishStockDisponible(any());
    }

    /**
     * Caso: el repositorio lanza excepción durante la validación.
     * Debe capturarse y publicar InventarioNoValidadoEvent.
     */
    @Test
    void validarInventarioCreacionOrden_publicaInventarioNoValidado_siRepoFalla() throws Exception {
        Long ordenId = 44L;
        var items = List.of(new OrdenCreadaEvent.ItemOrdenCreada(200L, 2));

        when(inventarioRepository.findById(200L)).thenThrow(new RuntimeException("DB down"));

        inventarioValidacionService.validarInventarioCreacionOrden(ordenId, items);

        verify(inventarioEventPublisher).publishStockNoDisponible(eventCaptor.capture());
        IntegrationEvent published = eventCaptor.getValue();
        assertThat(published).isInstanceOf(InventarioNoValidadoEvent.class);
        assertThat(((InventarioNoValidadoEvent) published).ordenId()).isEqualTo(ordenId);

        verify(inventarioEventPublisher, never()).publishStockDisponible(any());
    }
}
