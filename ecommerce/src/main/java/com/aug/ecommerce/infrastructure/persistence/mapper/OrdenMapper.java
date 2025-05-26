package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.model.orden.ItemOrden;
import com.aug.ecommerce.infrastructure.persistence.entity.ItemOrdenEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.OrdenEntity;

import java.util.List;
import java.util.stream.Collectors;

public class OrdenMapper {

    public static OrdenEntity toEntity(Orden orden) {
        OrdenEntity entity = new OrdenEntity();
        entity.setId(orden.getId());
        entity.setClienteId(orden.getClienteId());
        entity.setEstado(orden.getEstado());

        List<ItemOrdenEntity> items = orden.getItems().stream().map(item -> {
            ItemOrdenEntity e = new ItemOrdenEntity();
            e.setId(item.getId());
            e.setProductoId(item.getProductoId());
            e.setCantidad(item.getCantidad());
            e.setPrecioUnitario(item.getPrecioUnitario());
            return e;
        }).collect(Collectors.toList());

        entity.setItems(items);
        return entity;
    }

    public static Orden toDomain(OrdenEntity entity) {
        Orden orden = new Orden(entity.getId(), entity.getClienteId());
        entity.getItems().forEach(item -> {
            orden.agregarItem(item.getProductoId(), item.getCantidad(), item.getPrecioUnitario());
        });
        return orden;
    }
}
