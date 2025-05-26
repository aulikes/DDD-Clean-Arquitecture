package com.aug.ecommerce.infrastructure.persistence.repository.contract;


import com.aug.ecommerce.infrastructure.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaProductoCrudRepository extends JpaRepository<ProductoEntity, UUID> {
}
