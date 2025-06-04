package com.aug.ecommerce.adapters.rest.mapper;

import com.aug.ecommerce.adapters.rest.dto.CrearCategoriaRequestDTO;
import com.aug.ecommerce.adapters.rest.dto.CrearProductoRequestDTO;
import com.aug.ecommerce.application.command.CrearCategoriaCommand;
import com.aug.ecommerce.application.command.CrearProductoCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    CrearProductoCommand toCommand(CrearProductoRequestDTO dto);
    CrearProductoRequestDTO toDto(CrearProductoCommand command);
}
