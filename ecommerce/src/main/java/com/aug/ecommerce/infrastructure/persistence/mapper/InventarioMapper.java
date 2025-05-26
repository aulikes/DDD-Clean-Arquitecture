package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.model.inventario.Inventario;
import com.aug.ecommerce.infrastructure.persistence.entity.InventarioEntity;

public class InventarioMapper {

    private InventarioMapper() {}

    public static InventarioEntity toEntity(Inventario inv) {
        InventarioEntity e = new InventarioEntity();
        e.setId(inv.getProductoId());
        e.setProductoId(inv.getProductoId());
        e.setCantidadDisponible(inv.getStockDisponible());
        return e;
    }

    public static Inventario toDomain(InventarioEntity entity) {
        return new Inventario(entity.getProductoId(), entity.getCantidadDisponible());
    }
}
