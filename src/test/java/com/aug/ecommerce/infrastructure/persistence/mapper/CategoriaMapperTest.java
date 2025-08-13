package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.models.categoria.Categoria;
import com.aug.ecommerce.infrastructure.persistence.entity.CategoriaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para CategoriaMapper.
 * Verifica mapeos simples a entidad y dominio.
 */
class CategoriaMapperTest {

    @Test
    @DisplayName("toEntity: mapea todos los campos de Categoria a CategoriaEntity")
    void toEntity_ok() {
        var domain = new Categoria(10L, "Electrónica", "Desc");
        CategoriaEntity e = CategoriaMapper.toEntity(domain);

        assertEquals(10L, e.getId());
        assertEquals("Electrónica", e.getNombre());
        assertEquals("Desc", e.getDescripcion());
    }

    @Test
    @DisplayName("toDomain: mapea todos los campos de CategoriaEntity a Categoria")
    void toDomain_ok() {
        var e = new CategoriaEntity();
        e.setId(11L);
        e.setNombre("Hogar");
        e.setDescripcion("Desc hogar");

        Categoria domain = CategoriaMapper.toDomain(e);

        assertEquals(11L, domain.getId());
        assertEquals("Hogar", domain.getNombre());
        assertEquals("Desc hogar", domain.getDescripcion());
    }
}
