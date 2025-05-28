package com.aug.ecommerce.infrastructure.persistence.repository.contract;

import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaEnvioCrudRepository extends JpaRepository<EnvioEntity, Long> {
    List<EnvioEntity> findByOrdenId(Long ordenId);
}
