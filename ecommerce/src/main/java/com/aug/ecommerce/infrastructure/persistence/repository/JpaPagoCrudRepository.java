package com.aug.ecommerce.infrastructure.persistence.repository;

import com.aug.ecommerce.infrastructure.persistence.entity.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaPagoCrudRepository extends JpaRepository<PagoEntity, Long> {
    List<PagoEntity> findByOrdenId(Long ordenId);
}
