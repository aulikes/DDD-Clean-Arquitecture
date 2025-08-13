package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.commands.CrearProductoCommand;
import com.aug.ecommerce.application.events.ProductoCreadoEvent;
import com.aug.ecommerce.application.publishers.ProductoEventPublisher;
import com.aug.ecommerce.domain.models.categoria.Categoria;
import com.aug.ecommerce.domain.models.producto.Producto;
import com.aug.ecommerce.domain.repositories.CategoriaRepository;
import com.aug.ecommerce.domain.repositories.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductoService
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    ProductoRepository productoRepository;

    // Declarado en el servicio; no se usa en el flujo actual, pero se mockea por fidelidad
    @Mock
    CategoriaRepository categoriaRepository;

    @Mock
    ProductoEventPublisher productoEventPublisher;

    @InjectMocks
    ProductoService productoService;

    @Captor
    ArgumentCaptor<Producto> productoCaptor;

    @Captor
    ArgumentCaptor<ProductoCreadoEvent> eventoCaptor;

    /**
     * crearProducto(): construye Producto con id=null, lo guarda y publica evento con:
     *  - productoId = id de la instancia retornada por el repositorio
     *  - cantidad   = la del comando
     */
    @Test
    void crearProducto_debePersistirYPublicarEventoConIdRetornado() {
        // Dado: comando válido
        var cmd = new CrearProductoCommand(
                "Teclado Mecánico",
                "Switches azules, retroiluminado",
                199.99,
                "https://img/teclado.png",
                50L,
                3L
        );

        // Mock: categoría encontrada
        when(categoriaRepository.findById(3L)).thenReturn(
                java.util.Optional.of(new Categoria(3L, "Periféricos", "Teclados, ratones, etc."))
        );

        // Mock: repositorio de productos
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            return new Producto(
                    999L,                        // ID asignado por la capa de persistencia
                    p.getNombre(),
                    p.getDescripcion(),
                    p.getPrecio(),
                    p.getImagenUrl(),
                    p.getCategoriaId()
            );
        });

        // Cuando
        productoService.crearProducto(cmd);

        // Entonces: se guardó un Producto con los campos del comando y id=null antes de persistir
        verify(productoRepository).save(productoCaptor.capture());
        Producto guardado = productoCaptor.getValue();
        assertThat(guardado.getId()).isNull();
        assertThat(guardado.getNombre()).isEqualTo("Teclado Mecánico");
        assertThat(guardado.getDescripcion()).isEqualTo("Switches azules, retroiluminado");
        assertThat(guardado.getPrecio()).isEqualTo(199.99);
        assertThat(guardado.getImagenUrl()).isEqualTo("https://img/teclado.png");
        assertThat(guardado.getCategoriaId()).isEqualTo(3L);

        // Y: se publicó el evento con el ID retornado por save(...) y la cantidad del comando
        verify(productoEventPublisher).publicarProductoCreado(eventoCaptor.capture());
        ProductoCreadoEvent evt = eventoCaptor.getValue();
        assertThat(evt.productoId()).isEqualTo(999L);
        assertThat(evt.cantidad()).isEqualTo(50L);

        verifyNoMoreInteractions(productoRepository, productoEventPublisher);
    }

    /**
     * getAll(): retorna lo que devuelve el repositorio sin transformaciones.
     */
    @Test
    void getAll_debeRetornarListadoDelRepositorio() {
        when(productoRepository.findAll()).thenReturn(List.of(
                new Producto(1L, "A", "descA", 10.0, "imgA", 1L),
                new Producto(2L, "B", "descB", 20.0, "imgB", 2L)
        ));

        List<Producto> out = productoService.getAll();

        assertThat(out).hasSize(2);
        assertThat(out.get(0).getNombre()).isEqualTo("A");
        assertThat(out.get(1).getPrecio()).isEqualTo(20.0);

        verify(productoRepository).findAll();
        verifyNoMoreInteractions(productoRepository);
        verifyNoInteractions(productoEventPublisher, categoriaRepository);
    }

    @Test
    void crearProducto_categoriaInactiva_debeLanzarExcepcion() {
        // Dado: comando válido
        var cmd = new CrearProductoCommand(
                "Teclado Mecánico",
                "Switches azules, retroiluminado",
                199.99,
                "https://img/teclado.png",
                50L,
                3L
        );

        // Mock: categoría encontrada pero inactiva
        Categoria categoriaInactiva = new Categoria(3L, "Periféricos", "Teclados, ratones, etc.");
        categoriaInactiva.inactivar(); // inactiva
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(categoriaInactiva));

        // Cuando & Entonces: se espera IllegalStateException
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> productoService.crearProducto(cmd));

        assertEquals("La categoría está inactiva", ex.getMessage());

        // Verificar que NO se haya guardado el producto ni publicado el evento
        verify(productoRepository, never()).save(any());
        verify(productoEventPublisher, never()).publicarProductoCreado(any());
    }

}
