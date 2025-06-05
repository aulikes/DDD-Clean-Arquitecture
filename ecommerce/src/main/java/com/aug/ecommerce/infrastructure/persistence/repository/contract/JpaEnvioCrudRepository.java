package com.aug.ecommerce.infrastructure.persistence.repository.contract;

import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaEnvioCrudRepository extends JpaRepository<EnvioEntity, Long> {
    List<EnvioEntity> findByOrdenId(Long ordenId);

    @Query("select e from EnvioEntity e where e.estado = :estado and e.intentos <= :intentos")
    List<EnvioEntity> findByEstado (@Param("estado") EnvioEntity.Estado estado, @Param("intentos") int intentos);
}
