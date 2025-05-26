package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.model.cliente.Cliente;
import com.aug.ecommerce.infrastructure.persistence.entity.ClienteEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteEntity toEntity(Cliente cliente);
    Cliente toDomain(ClienteEntity entity);
}
