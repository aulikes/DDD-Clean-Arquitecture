package com.aug.ecommerce.adapters.rest.mapper;

import com.aug.ecommerce.adapters.rest.dtos.CrearProductoRequestDTO;
import com.aug.ecommerce.application.commands.CrearProductoCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    CrearProductoCommand toCommand(CrearProductoRequestDTO dto);
    CrearProductoRequestDTO toDto(CrearProductoCommand command);
}
