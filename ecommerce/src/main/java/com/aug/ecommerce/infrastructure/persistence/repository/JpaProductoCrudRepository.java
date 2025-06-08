package com.aug.ecommerce.infrastructure.persistence.repository;


import com.aug.ecommerce.infrastructure.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductoCrudRepository extends JpaRepository<ProductoEntity, Long> {
}
