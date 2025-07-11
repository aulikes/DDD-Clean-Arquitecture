package com.aug.ecommerce.adapters.rest.mapper;

import com.aug.ecommerce.adapters.rest.dtos.CrearClienteRequestDTO;
import com.aug.ecommerce.application.commands.CrearClienteCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    CrearClienteCommand toCommand(CrearClienteRequestDTO dto);
    CrearClienteCommand.Direccion toItem(CrearClienteRequestDTO.Direccion dto);
}
