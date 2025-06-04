package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.model.pago.EstadoPago;
import com.aug.ecommerce.domain.model.pago.Pago;
import com.aug.ecommerce.infrastructure.persistence.entity.PagoEntity;

public class PagoMapper {

    public static PagoEntity toEntity(Pago pago) {
        PagoEntity entity = new PagoEntity();
        entity.setId(pago.getId());
        entity.setOrdenId(pago.getOrdenId());
        entity.setMonto(pago.getMonto());
        entity.setMetodo(pago.getMetodo());
        entity.setEstado(PagoEntity.Estado.valueOf(pago.getEstado().name()));
        entity.setCodigoTransaccion(pago.getCodigoTransaccion());
        entity.setMensajeError(pago.getMensajeError());
        return entity;
    }

    public static Pago toDomain(PagoEntity entity) {
        return Pago.fromPersistence(
                entity.getId(),
                entity.getOrdenId(),
                entity.getMonto(),
                entity.getMetodo(),
                EstadoPago.valueOf(entity.getEstado().name()),
                entity.getCodigoTransaccion(),
                entity.getMensajeError()
        );
    }
}
