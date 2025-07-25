package com.aug.ecommerce.infrastructure.persistence.adapter;

import com.aug.ecommerce.domain.models.envio.Envio;
import com.aug.ecommerce.domain.models.envio.EstadoEnvio;
import com.aug.ecommerce.domain.repositories.EnvioRepository;
import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.enums.EstadoEnvioEntity;
import com.aug.ecommerce.infrastructure.persistence.mapper.EnvioMapper;
import com.aug.ecommerce.infrastructure.persistence.repository.JpaEnvioCrudRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EnvioRepositoryAdapter implements EnvioRepository {

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
    public List<Envio> findByEstado(EstadoEnvio estadoEnvio) {
        var entityEstado = EstadoEnvioEntity.valueOf(estadoEnvio.name());
        List<EnvioEntity> listEnvEnt = jpa.findByEstado(entityEstado);
        return listEnvEnt.stream().map(EnvioMapper::toDomainWithHistorial).toList();
    }
}
