package com.aug.ecommerce.infrastructure.persistence.repository.contract;

import com.aug.ecommerce.infrastructure.persistence.entity.OrdenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface JpaOrdenCrudRepository extends JpaRepository<OrdenEntity, UUID> {
    List<OrdenEntity> findByClienteId(UUID clienteId);
}
