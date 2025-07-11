package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.models.categoria.Categoria;
import com.aug.ecommerce.infrastructure.persistence.entity.CategoriaEntity;

public class CategoriaMapper {

    public static CategoriaEntity toEntity(Categoria categoria) {
        CategoriaEntity e = new CategoriaEntity();
        e.setId(categoria.getId());
        e.setNombre(categoria.getNombre());
        e.setDescripcion(categoria.getDescripcion());
        return e;
    }

    public static Categoria toDomain(CategoriaEntity entity) {
        return new Categoria(entity.getId(), entity.getNombre(), entity.getDescripcion());
    }
}
