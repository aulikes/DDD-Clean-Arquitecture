package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.models.producto.Producto;
import com.aug.ecommerce.infrastructure.persistence.entity.ProductoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ProductoMapper.
 * Verifica mapeo completo (incluye precio, imagen y categoría).
 */
class ProductoMapperTest {

    @Test
    @DisplayName("toEntity: mapea todos los campos de Producto a ProductoEntity")
    void toEntity_ok() {
        var p = new Producto(7L, "Mouse", "Gamer", 99.9, "url", 3L);

        ProductoEntity e = ProductoMapper.toEntity(p);

        assertEquals(7L, e.getId());
        assertEquals("Mouse", e.getNombre());
        assertEquals("Gamer", e.getDescripcion());
        assertEquals(99.9, e.getPrecio());
        assertEquals("url", e.getImagenUrl());
        assertEquals(3L, e.getCategoriaId());
    }

    @Test
    @DisplayName("toDomain: mapea todos los campos de ProductoEntity a Producto")
    void toDomain_ok() {
        var e = new ProductoEntity();
        e.setId(8L); e.setNombre("Teclado"); e.setDescripcion("Mecánico");
        e.setPrecio(49.5); e.setImagenUrl("u"); e.setCategoriaId(4L);

        Producto p = ProductoMapper.toDomain(e);

        assertEquals(8L, p.getId());
        assertEquals("Teclado", p.getNombre());
        assertEquals("Mecánico", p.getDescripcion());
        assertEquals(49.5, p.getPrecio());
        assertEquals("u", p.getImagenUrl());
        assertEquals(4L, p.getCategoriaId());
    }
}
