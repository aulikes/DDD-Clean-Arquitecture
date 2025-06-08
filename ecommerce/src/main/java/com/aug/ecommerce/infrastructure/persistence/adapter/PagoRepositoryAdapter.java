package com.aug.ecommerce.infrastructure.persistence.adapter;

import com.aug.ecommerce.domain.model.pago.Pago;
import com.aug.ecommerce.domain.repository.PagoRepository;
import com.aug.ecommerce.infrastructure.persistence.entity.PagoEntity;
import com.aug.ecommerce.infrastructure.persistence.mapper.PagoMapper;
import com.aug.ecommerce.infrastructure.persistence.repository.JpaPagoCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PagoRepositoryAdapter implements PagoRepository {

    private final JpaPagoCrudRepository jpa;

    @Override
    public Pago save(Pago pago) {
        PagoEntity entity = PagoMapper.toEntity(pago);
        return PagoMapper.toDomain(jpa.save(entity));
    }

    @Override
    public Optional<Pago> findById(Long id) {
        return jpa.findById(id).map(PagoMapper::toDomain);
    }

    @Override
    public List<Pago> findByOrdenId(Long ordenId) {
        return jpa.findByOrdenId(ordenId).stream().map(PagoMapper::toDomain).toList();
    }

}
