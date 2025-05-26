package com.aug.ecommerce.infrastructure.persistence.repository.contract;

import com.aug.ecommerce.infrastructure.persistence.entity.InventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaInventarioCrudRepository extends JpaRepository<InventarioEntity, UUID> {
    Optional<InventarioEntity> findByProductoId(UUID productoId);
}
