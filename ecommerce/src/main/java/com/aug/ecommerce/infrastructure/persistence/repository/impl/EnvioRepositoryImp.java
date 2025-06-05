package com.aug.ecommerce.infrastructure.persistence.repository.impl;

import com.aug.ecommerce.domain.model.envio.Envio;
import com.aug.ecommerce.domain.model.envio.EstadoEnvio;
import com.aug.ecommerce.domain.repository.EnvioRepository;
import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEntity;
import com.aug.ecommerce.infrastructure.persistence.mapper.EnvioMapper;
import com.aug.ecommerce.infrastructure.persistence.repository.contract.JpaEnvioCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EnvioRepositoryImp implements EnvioRepository {

    private final JpaEnvioCrudRepository jpa;

    @Override
    public Envio save(Envio envio) {
        return EnvioMapper.toDomain(jpa.save(EnvioMapper.toEntity(envio)));
    }

    @Override
    public Optional<Envio> findById(Long id) {
        return jpa.findById(id).map(EnvioMapper::toDomain);
    }

    @Override
    public List<Envio> findByOrdenId(Long ordenId) {
        return jpa.findByOrdenId(ordenId).stream().map(EnvioMapper::toDomain).toList();
    }

    @Override
    public List<Envio> findByEstado(EstadoEnvio estadoEnvio, int maxIntentos) {
        var entityEstado = EnvioEntity.Estado.valueOf(estadoEnvio.name());
        return jpa.findByEstado(entityEstado, maxIntentos).stream().map(EnvioMapper::toDomain).toList();
    }
}
