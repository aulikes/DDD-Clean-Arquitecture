package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.models.orden.EstadoOrden;
import com.aug.ecommerce.domain.models.orden.Orden;
import com.aug.ecommerce.infrastructure.persistence.entity.ItemOrdenEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.OrdenEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrdenMapper {

    private OrdenMapper() {}

    public static OrdenEntity toEntity(Orden orden) {
        OrdenEntity entity = new OrdenEntity();
        entity.setId(orden.getId());
        entity.setClienteId(orden.getClienteId());
        entity.setEstado(orden.getEstado().getValor());
        entity.setDireccionEnviar(orden.getDireccionEnviar());
        entity.setError(orden.getError());

        List<ItemOrdenEntity> items = orden.getItems().stream().map(item -> {
            ItemOrdenEntity e = new ItemOrdenEntity();
            e.setId(item.getId());
            e.setOrden(entity);
            e.setProductoId(item.getProductoId());
            e.setCantidad(item.getCantidad());
            e.setPrecioUnitario(item.getPrecioUnitario());
            return e;
        }).collect(Collectors.toList());

        entity.setItems(items);
        return entity;
    }

    public static Orden toDomain(OrdenEntity entity) {
        Orden orden = Orden.fromPersistence(entity.getId(), entity.getClienteId(),
                entity.getDireccionEnviar(), new ArrayList<>(),
                EstadoOrden.desde(entity.getEstado()), entity.getError()
                );

        entity.getItems().forEach(item -> orden.restoreItem(
                item.getId(),
                item.getProductoId(),
                item.getCantidad(),
                item.getPrecioUnitario()));
        return orden;
    }
}
