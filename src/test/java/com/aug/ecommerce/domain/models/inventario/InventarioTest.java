package com.aug.ecommerce.domain.models.inventario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InventarioTest {

    @Test
    @DisplayName("Crear inventario valida stock inicial y productoId")
    void crearInventarioValido() {
        assertThrows(NullPointerException.class, () -> new Inventario(null, 0L));
        assertThrows(IllegalArgumentException.class, () -> new Inventario(1L, -1L));
        var inv = new Inventario(1L, 5L);
        assertEquals(5L, inv.getStockDisponible());
    }

    @Test
    @DisplayName("Aumentar/Disminuir stock validan valores y límites")
    void aumentarDisminuir() {
        var inv = new Inventario(1L, 5L);

        assertThrows(IllegalArgumentException.class, () -> inv.aumentarStock(0L));
        inv.aumentarStock(3L);
        assertEquals(8L, inv.getStockDisponible());

        assertThrows(IllegalArgumentException.class, () -> inv.disminuirStock(0L));
        assertThrows(IllegalStateException.class, () -> inv.disminuirStock(9L));
        inv.disminuirStock(3L);
        assertEquals(5L, inv.getStockDisponible());
    }

    @Test
    @DisplayName("tieneStockDisponible devuelve verdadero cuando alcanza")
    void tieneStockDisponible() {
        var inv = new Inventario(1L, 10L);
        assertTrue(inv.tieneStockDisponible(10));
        assertTrue(inv.tieneStockDisponible(1));
        assertFalse(inv.tieneStockDisponible(11));
        assertFalse(inv.tieneStockDisponible(0));
    }
}
