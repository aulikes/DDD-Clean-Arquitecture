package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.models.envio.Envio;
import com.aug.ecommerce.domain.models.envio.EstadoEnvio;
import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEstadoHistorialEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.enums.EstadoEnvioEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnvioMapper {

    private EnvioMapper() {}

    public static EnvioEntity toEntity(Envio envio) {
        EnvioEntity entity = new EnvioEntity();
        entity.setId(envio.getId());
        entity.setOrdenId(envio.getOrdenId());
        entity.setDireccionEnvio(envio.getDireccionEnvio());
        entity.setTrackingNumber(envio.getTrackingNumber());
        entity.setEstado(EstadoEnvioEntity.valueOf(envio.getEstado().name()));
        entity.setIntentos(envio.getIntentos());
        entity.setRazonFallo(envio.getRazonFallo());
        return entity;
    }

    public static EnvioEntity toEntityWithHistorial(Envio envio) {
        EnvioEntity entity = toEntity(envio);

        List<EnvioEstadoHistorialEntity> historialEntities = envio.getHistorial().stream()
                .map(h -> {
                    EnvioEstadoHistorialEntity histEntity = new EnvioEstadoHistorialEntity();
                    histEntity.setId(h.getId());
                    histEntity.setEstado(EstadoEnvioEntity.valueOf(h.getEstadoEnvio().name()));
                    histEntity.setObservacion(h.getObservacion());
                    histEntity.setFechaCambio(h.getFechaCambio());
                    histEntity.setEnvio(entity); // establecer la relación inversa
                    return histEntity;
                })
                .collect(Collectors.toList());

        entity.setHistorial(historialEntities);

        return entity;
    }

    public static Envio toDomain(EnvioEntity entity) {
        return Envio.fromPersistence(
                entity.getId(),
                entity.getOrdenId(),
                entity.getDireccionEnvio(),
                EstadoEnvio.valueOf(entity.getEstado().name()),
                entity.getTrackingNumber(),
                entity.getRazonFallo(),
                entity.getIntentos(),
                new ArrayList<>()
        );
    }

    public static Envio toDomainWithHistorial(EnvioEntity entity) {
        Envio envio = toDomain(entity);

        entity.getHistorial().forEach(h -> envio.restoreHistorial(
                h.getId(),
                EstadoEnvio.valueOf(h.getEstado().name()),
                h.getObservacion(),
                h.getFechaCambio()));
        return envio;
    }
}
