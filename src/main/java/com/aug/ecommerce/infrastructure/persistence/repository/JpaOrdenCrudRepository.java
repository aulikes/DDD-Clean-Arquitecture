package com.aug.ecommerce.infrastructure.persistence.repository;

import com.aug.ecommerce.infrastructure.persistence.entity.OrdenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaOrdenCrudRepository extends JpaRepository<OrdenEntity, Long> {
    List<OrdenEntity> findByClienteId(Long clienteId);
}
