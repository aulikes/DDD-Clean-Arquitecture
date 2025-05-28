package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.model.envio.Envio;
import com.aug.ecommerce.domain.model.envio.EstadoEnvio;
import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEntity;

public class EnvioMapper {

    private EnvioMapper(){ }

    public static EnvioEntity toEntity(Envio envio) {
        EnvioEntity entity = new EnvioEntity();
        entity.setId(envio.getId());
        entity.setOrdenId(envio.getOrdenId());
        entity.setDireccionEnvio(envio.getDireccionEnvio());
        entity.setTrackingNumber(envio.getTrackingNumber());
        entity.setEstado(EnvioEntity.Estado.valueOf(envio.getEstado().name()));
        return entity;
    }

    public static Envio toDomain(EnvioEntity entity) {
        return Envio.fromPersistence(
                entity.getId(),
                entity.getOrdenId(),
                entity.getDireccionEnvio(),
                EstadoEnvio.valueOf(entity.getEstado().name()),
                entity.getTrackingNumber()
        );
    }
}
