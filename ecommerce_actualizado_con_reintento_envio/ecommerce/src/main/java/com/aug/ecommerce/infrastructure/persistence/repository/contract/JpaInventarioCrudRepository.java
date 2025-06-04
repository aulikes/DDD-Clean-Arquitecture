package com.aug.ecommerce.infrastructure.persistence.repository.contract;

import com.aug.ecommerce.infrastructure.persistence.entity.InventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaInventarioCrudRepository extends JpaRepository<InventarioEntity, Long> {
    Optional<InventarioEntity> findByProductoId(Long productoId);
}
