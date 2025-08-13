package com.aug.ecommerce.domain.models.orden;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemOrdenTest {

    @Test
    @DisplayName("create() inicializa y getSubtotal multiplica cantidad*precio")
    void createYSubtotal() {
        var item = ItemOrden.create(101L, 2, 15.5);
        assertEquals(31.0, item.getSubtotal(), 0.0001);
        assertEquals(2, item.getCantidad());
        assertEquals(101L, item.getProductoId());
    }

    @Test
    @DisplayName("cambiarCantidad valida sea > 0")
    void cambiarCantidadValida() {
        var item = ItemOrden.create(1L, 1, 10.0);
        assertThrows(IllegalArgumentException.class, () -> item.cambiarCantidad(0));
        item.cambiarCantidad(5);
        assertEquals(5, item.getCantidad());
    }
}
