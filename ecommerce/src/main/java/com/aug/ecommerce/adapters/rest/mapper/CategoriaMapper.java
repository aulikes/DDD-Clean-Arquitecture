package com.aug.ecommerce.adapters.rest.mapper;

import com.aug.ecommerce.adapters.rest.dto.CrearCategoriaRequestDTO;
import com.aug.ecommerce.application.command.CrearCategoriaCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    CrearCategoriaCommand toCommand(CrearCategoriaRequestDTO dto);
    CrearCategoriaRequestDTO toDto(CrearCategoriaCommand command);
}
