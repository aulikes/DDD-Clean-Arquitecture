package com.aug.ecommerce.infrastructure.persistence.repository.impl;

import com.aug.ecommerce.domain.model.envio.Envio;
import com.aug.ecommerce.domain.model.envio.EstadoEnvio;
import com.aug.ecommerce.domain.repository.EnvioRepository;
import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.enums.EstadoEnvioEntity;
import com.aug.ecommerce.infrastructure.persistence.mapper.EnvioMapper;
import com.aug.ecommerce.infrastructure.persistence.repository.contract.JpaEnvioCrudRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EnvioRepositoryImp implements EnvioRepository {

    private final JpaEnvioCrudRepository jpa;

    @Override
    public Envio saveWithHistorial(Envio envio) {
        EnvioEntity entity = jpa.save(EnvioMapper.toEntityWithHistorial(envio));
        EnvioEntity opcEntity = jpa.findByIdWithHistorial(entity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Error al buscar el envio con su historial"));
        return EnvioMapper.toDomainWithHistorial(opcEntity);
    }

    @Override
    public Optional<Envio> findById(Long id) {
        return jpa.findById(id).map(EnvioMapper::toDomain);
    }

    @Override
    public Optional<Envio> findByIdWithHistorial(Long id) {
        jpa.findByIdWithHistorial(id);
        return Optional.empty();
    }

    @Override
    public List<Envio> findByOrdenId(Long ordenId) {
        return jpa.findByOrdenId(ordenId).stream().map(EnvioMapper::toDomain).toList();
    }

    @Override
    public List<Envio> findByEstado(EstadoEnvio estadoEnvio, int maxIntentos) {
        var entityEstado = EstadoEnvioEntity.valueOf(estadoEnvio.name());
        return jpa.findByEstado(entityEstado, maxIntentos).stream().map(EnvioMapper::toDomain).toList();
    }
}
