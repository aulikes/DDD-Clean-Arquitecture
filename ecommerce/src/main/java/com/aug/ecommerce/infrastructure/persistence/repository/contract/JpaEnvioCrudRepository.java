package com.aug.ecommerce.infrastructure.persistence.repository.contract;

import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.enums.EstadoEnvioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaEnvioCrudRepository extends JpaRepository<EnvioEntity, Long> {
    List<EnvioEntity> findByOrdenId(Long ordenId);

    @Query("select e from EnvioEntity e where e.estado = :estado and e.intentos <= :intentos")
    List<EnvioEntity> findByEstado (@Param("estado") EstadoEnvioEntity estado, @Param("intentos") int intentos);

    @Query("SELECT e FROM EnvioEntity e LEFT JOIN FETCH e.historial WHERE e.id = :id")
    Optional<EnvioEntity> findByIdWithHistorial(@Param("id") Long id);
}
