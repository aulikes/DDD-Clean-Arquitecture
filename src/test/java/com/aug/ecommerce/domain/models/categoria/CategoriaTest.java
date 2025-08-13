package com.aug.ecommerce.domain.models.categoria;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // Se usa JUnit 5 + Mockito (sin @MockBean)
class CategoriaTest {

    @Test
    @DisplayName("Crear categoría deja activa=true y valida nulos")
    void crearCategoria_validaCamposYActivaPorDefecto() {
        // Se valida que el constructor asigne activa=true y no permita nulls
        var cat = new Categoria(1L, "Electrónica", "Gadgets y más");
        assertTrue(cat.isActiva(), "La categoría debe quedar activa por defecto");

        assertThrows(NullPointerException.class, () -> new Categoria(2L, null, "x"));
        assertThrows(NullPointerException.class, () -> new Categoria(3L, "x", null));
    }

    @Test
    @DisplayName("Activar/Inactivar alterna el estado correctamente")
    void activarInactivar() {
        var cat = new Categoria(1L, "Libros", "Lectura");
        cat.inactivar();
        assertFalse(cat.isActiva(), "Debe quedar inactiva");

        cat.activar();
        assertTrue(cat.isActiva(), "Debe volver a activa");
    }

    @Test
    @DisplayName("Setters de nombre/descripcion no permiten null")
    void settersNoPermitenNull() {
        var cat = new Categoria(1L, "Original", "Desc");
        assertThrows(NullPointerException.class, () -> cat.setNombre(null));
        assertThrows(NullPointerException.class, () -> cat.setDescripcion(null));
    }
}
