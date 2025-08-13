package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.models.inventario.Inventario;
import com.aug.ecommerce.infrastructure.persistence.entity.InventarioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para InventarioMapper.
 * Verifica mapeo entre stock disponible y cantidad.
 */
class InventarioMapperTest {

    @Test
    @DisplayName("toEntity: productoId y stockDisponible -> productoId y cantidadDisponible")
    void toEntity_ok() {
        var inv = new Inventario(55L, 120L);

        InventarioEntity e = InventarioMapper.toEntity(inv);

        assertEquals(55L, e.getProductoId());
        assertEquals(120L, e.getCantidadDisponible());
    }

    @Test
    @DisplayName("toDomain: cantidadDisponible -> stockDisponible")
    void toDomain_ok() {
        var e = new InventarioEntity();
        e.setProductoId(44L);
        e.setCantidadDisponible(10L);

        Inventario inv = InventarioMapper.toDomain(e);

        assertEquals(44L, inv.getProductoId());
        assertEquals(10L, inv.getStockDisponible());
    }
}
