package com.aug.ecommerce.adapters.rest.mapper;

import com.aug.ecommerce.adapters.rest.dtos.CrearCategoriaRequestDTO;
import com.aug.ecommerce.application.commands.CrearCategoriaCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    CrearCategoriaCommand toCommand(CrearCategoriaRequestDTO dto);
    CrearCategoriaRequestDTO toDto(CrearCategoriaCommand command);
}
