package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.events.IntegrationEvent;
import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.events.ProductoNoValidadoEvent;
import com.aug.ecommerce.application.events.ProductoValidadoEvent;
import com.aug.ecommerce.application.publishers.ProductoEventPublisher;
import com.aug.ecommerce.domain.models.producto.Producto;
import com.aug.ecommerce.domain.repositories.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Pruebas para ProductoValidacionService (implementación real).
 * No se valida orden; se verifican interacciones y eventos publicados.
 */
@ExtendWith(MockitoExtension.class)
class ProductoValidacionServiceTest {

    @Mock
    ProductoRepository productoRepository;

    @Mock
    ProductoEventPublisher productoEventPublisher;

    @InjectMocks
    ProductoValidacionService productoValidacionService;

    @Captor
    ArgumentCaptor<IntegrationEvent> eventCaptor;

    /**
     * Todos los productos existen -> publica ProductoValidadoEvent(ordenId).
     */
    @Test
    void validarProductoCreacionOrden_publicaProductoValido_siTodosExisten() throws Exception {
        Long ordenId = 99L;
        var items = List.of(
                new OrdenCreadaEvent.ItemOrdenCreada(1L, 2),
                new OrdenCreadaEvent.ItemOrdenCreada(2L, 1)
        );

        when(productoRepository.findById(1L)).thenReturn(Optional.of(
                new Producto(1L, "P1", "D1", 10.0, "img", 3L)
        ));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(
                new Producto(2L, "P2", "D2", 20.0, "img", 3L)
        ));

        productoValidacionService.validarProductoCreacionOrden(ordenId, items);

        verify(productoRepository).findById(1L);
        verify(productoRepository).findById(2L);

        verify(productoEventPublisher).publishProductoValido(eventCaptor.capture());
        IntegrationEvent ev = eventCaptor.getValue();
        assertThat(ev).isInstanceOf(ProductoValidadoEvent.class);
        assertThat(((ProductoValidadoEvent) ev).ordenId()).isEqualTo(ordenId);

        verify(productoEventPublisher, never()).publishProductoNoValido(any());
    }

    /**
     * Alguno no existe -> publica ProductoNoValidadoEvent(ordenId).
     */
    @Test
    void validarProductoCreacionOrden_publicaProductoNoValido_siAlgunoNoExiste() throws Exception {
        Long ordenId = 77L;
        var items = List.of(
                new OrdenCreadaEvent.ItemOrdenCreada(10L, 1),
                new OrdenCreadaEvent.ItemOrdenCreada(11L, 1)
        );

        when(productoRepository.findById(10L)).thenReturn(Optional.of(
                new Producto(10L, "X", "DX", 5.0, "img", 1L)
        ));
        when(productoRepository.findById(11L)).thenReturn(Optional.empty()); // no existe

        productoValidacionService.validarProductoCreacionOrden(ordenId, items);

        verify(productoEventPublisher).publishProductoNoValido(eventCaptor.capture());
        IntegrationEvent ev = eventCaptor.getValue();
        assertThat(ev).isInstanceOf(ProductoNoValidadoEvent.class);
        assertThat(((ProductoNoValidadoEvent) ev).ordenId()).isEqualTo(ordenId);

        verify(productoEventPublisher, never()).publishProductoValido(any());
    }

    /**
     * El repositorio lanza excepción -> publica ProductoNoValidadoEvent(ordenId).
     */
    @Test
    void validarProductoCreacionOrden_publicaProductoNoValido_siRepoFalla() throws Exception {
        Long ordenId = 55L;
        var items = List.of(new OrdenCreadaEvent.ItemOrdenCreada(100L, 1));

        when(productoRepository.findById(100L)).thenThrow(new RuntimeException("DB down"));

        productoValidacionService.validarProductoCreacionOrden(ordenId, items);

        verify(productoEventPublisher).publishProductoNoValido(eventCaptor.capture());
        IntegrationEvent ev = eventCaptor.getValue();
        assertThat(ev).isInstanceOf(ProductoNoValidadoEvent.class);
        assertThat(((ProductoNoValidadoEvent) ev).ordenId()).isEqualTo(ordenId);

        verify(productoEventPublisher, never()).publishProductoValido(any());
    }

    /**
     * Lista de items vacía (allMatch sobre vacío es true) -> publica ProductoValidadoEvent.
     */
    @Test
    void validarProductoCreacionOrden_publicaProductoValido_siListaVacia() throws Exception {
        Long ordenId = 123L;
        List<OrdenCreadaEvent.ItemOrdenCreada> items = List.of(); // no hay productos que validar

        productoValidacionService.validarProductoCreacionOrden(ordenId, items);

        verify(productoEventPublisher).publishProductoValido(eventCaptor.capture());
        IntegrationEvent ev = eventCaptor.getValue();
        assertThat(ev).isInstanceOf(ProductoValidadoEvent.class);
        assertThat(((ProductoValidadoEvent) ev).ordenId()).isEqualTo(ordenId);

        verify(productoEventPublisher, never()).publishProductoNoValido(any());
        verifyNoInteractions(productoRepository); // no se consulta nada si no hay items
    }
}
