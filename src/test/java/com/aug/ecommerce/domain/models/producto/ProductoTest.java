package com.aug.ecommerce.domain.models.producto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductoTest {

    @Test
    @DisplayName("Setters validan precio e imagenUrl")
    void validaPrecioEImagen() {
        var p = new Producto(1L, "Audífonos", "BT", 100.0, "http://img", 10L);

        assertThrows(IllegalArgumentException.class, () -> p.setPrecio(-1.0));
        assertThrows(IllegalArgumentException.class, () -> p.setPrecio(null));
        assertDoesNotThrow(() -> p.setPrecio(0.0));

        assertThrows(NullPointerException.class, () -> p.setImagenUrl(null));
        p.setImagenUrl("http://nuevo");
        assertEquals("http://nuevo", p.getImagenUrl());
    }
}
