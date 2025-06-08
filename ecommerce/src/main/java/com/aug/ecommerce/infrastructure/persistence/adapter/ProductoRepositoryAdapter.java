package com.aug.ecommerce.infrastructure.persistence.adapter;

import com.aug.ecommerce.domain.model.producto.Producto;
import com.aug.ecommerce.domain.repository.ProductoRepository;
import com.aug.ecommerce.infrastructure.persistence.mapper.ProductoMapper;
import com.aug.ecommerce.infrastructure.persistence.repository.JpaProductoCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ProductoRepositoryAdapter implements ProductoRepository {

    private final JpaProductoCrudRepository jpa;

    @Override
    public Producto save(Producto producto) {
        return ProductoMapper.toDomain(jpa.save(ProductoMapper.toEntity(producto)));
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return jpa.findById(id).map(ProductoMapper::toDomain);
    }

    @Override
    public List<Producto> findAll() {
        return jpa.findAll().stream().map(ProductoMapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
