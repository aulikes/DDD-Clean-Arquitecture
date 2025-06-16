package com.aug.ecommerce.infrastructure.persistence.repository;

import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.enums.EstadoEnvioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaEnvioCrudRepository extends JpaRepository<EnvioEntity, Long> {
    List<EnvioEntity> findByOrdenId(Long ordenId);

    @Query("SELECT e FROM EnvioEntity e LEFT JOIN FETCH e.historial WHERE e.estado = :estado")
    List<EnvioEntity> findByEstado (@Param("estado") EstadoEnvioEntity estado);

    @Query("SELECT e FROM EnvioEntity e LEFT JOIN FETCH e.historial WHERE e.id = :id")
    Optional<EnvioEntity> findByIdWithHistorial(@Param("id") Long id);
}
