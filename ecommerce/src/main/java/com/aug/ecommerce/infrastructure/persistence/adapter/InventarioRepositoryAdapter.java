package com.aug.ecommerce.infrastructure.persistence.adapter;

import com.aug.ecommerce.domain.model.inventario.Inventario;
import com.aug.ecommerce.domain.repository.InventarioRepository;
import com.aug.ecommerce.infrastructure.persistence.mapper.InventarioMapper;
import com.aug.ecommerce.infrastructure.persistence.repository.JpaInventarioCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class InventarioRepositoryAdapter implements InventarioRepository {

    private final JpaInventarioCrudRepository jpa;

    @Override
    public void save(Inventario inventario) {
        jpa.save(InventarioMapper.toEntity(inventario));
    }

    @Override
    public Optional<Inventario> findById(Long id) {
        return jpa.findById(id).map(InventarioMapper::toDomain);
    }

    @Override
    public List<Inventario> findAll() {
        return jpa.findAll().stream().map(InventarioMapper::toDomain).toList();
    }

}
