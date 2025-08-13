package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.commands.CrearInventarioCommand;
import com.aug.ecommerce.domain.models.inventario.Inventario;
import com.aug.ecommerce.domain.repositories.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Estas pruebas validan el comportamiento real de InventarioService
 */
@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    InventarioRepository inventarioRepository;

    @InjectMocks
    InventarioService inventarioService;

    @Captor
    ArgumentCaptor<Inventario> inventarioCaptor;

    /**
     * Caso: existe inventario para el producto.
     * Debe llamar aumentarStock(cantidad) sobre la instancia recuperada y persistir ESA MISMA instancia.
     */
    @Test
    void crearInvenario_debeAumentarStockYSguardarMismaInstancia_siExiste() {
        // Dado
        Long productoId = 10L;
        Long cantidad = 15L;
        Inventario existente = spy(new Inventario(productoId, 5L)); // se espía para verificar aumentarStock

        when(inventarioRepository.findById(productoId)).thenReturn(Optional.of(existente));
        doNothing().when(inventarioRepository).save(any(Inventario.class));

        // Cuando
        inventarioService.crearInvenario(new CrearInventarioCommand(productoId, cantidad));

        // Entonces: se incrementó el stock en la instancia recuperada
        verify(existente).aumentarStock(cantidad);

        // Y: se guardó la MISMA instancia (no una nueva)
        verify(inventarioRepository).save(inventarioCaptor.capture());
        Inventario guardado = inventarioCaptor.getValue();
        assertThat(guardado).isSameAs(existente);

        // Interacciones esperadas con el repositorio
        verify(inventarioRepository).findById(productoId);
        verifyNoMoreInteractions(inventarioRepository);
    }

    /**
     * Caso: no existe inventario para el producto.
     * Debe crear un nuevo Inventario con (productoId, cantidad) y guardarlo.
     */
    @Test
    void crearInvenario_debeCrearNuevoYGuardar_siNoExiste() {
        // Dado
        Long productoId = 20L;
        Long cantidad = 8L;

        when(inventarioRepository.findById(productoId)).thenReturn(Optional.empty());
        doNothing().when(inventarioRepository).save(any(Inventario.class));

        // Cuando
        inventarioService.crearInvenario(new CrearInventarioCommand(productoId, cantidad));

        // Entonces: se guardó un nuevo Inventario con valores del comando
        verify(inventarioRepository).save(inventarioCaptor.capture());
        Inventario guardado = inventarioCaptor.getValue();

        assertThat(guardado.getProductoId()).isEqualTo(productoId);
        assertThat(guardado.getStockDisponible()).isEqualTo(cantidad);

        // Interacciones esperadas con el repositorio
        verify(inventarioRepository).findById(productoId);
        verifyNoMoreInteractions(inventarioRepository);
    }

    /**
     * Debe retornar el listado que entrega el repositorio sin transformaciones adicionales.
     */
    @Test
    void getAll_debeRetornarListadoDelRepositorio() {
        // Dado
        when(inventarioRepository.findAll()).thenReturn(List.of(
                new Inventario(1L, 3L),
                new Inventario(2L, 5L)
        ));

        // Cuando
        List<Inventario> all = inventarioService.getAll();

        // Entonces
        assertThat(all).hasSize(2);
        assertThat(all.get(0).getProductoId()).isEqualTo(1L);
        assertThat(all.get(0).getStockDisponible()).isEqualTo(3L);

        verify(inventarioRepository).findAll();
        verifyNoMoreInteractions(inventarioRepository);
    }
}
