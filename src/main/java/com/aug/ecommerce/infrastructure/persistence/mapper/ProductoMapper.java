package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.models.producto.Producto;
import com.aug.ecommerce.infrastructure.persistence.entity.ProductoEntity;

public class ProductoMapper {

    private ProductoMapper() {}

    public static ProductoEntity toEntity(Producto producto) {
        ProductoEntity e = new ProductoEntity();
        e.setId(producto.getId());
        e.setNombre(producto.getNombre());
        e.setDescripcion(producto.getDescripcion());
        e.setPrecio(producto.getPrecio());
        e.setImagenUrl(producto.getImagenUrl());
        e.setCategoriaId(producto.getCategoriaId());
        return e;
    }

    public static Producto toDomain(ProductoEntity entity) {

        return new Producto(
                entity.getId(), entity.getNombre(), entity.getDescripcion(),
                entity.getPrecio(), entity.getImagenUrl(), entity.getCategoriaId());
    }
}
