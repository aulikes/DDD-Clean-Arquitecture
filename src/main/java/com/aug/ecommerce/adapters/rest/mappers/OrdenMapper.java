package com.aug.ecommerce.adapters.rest.mappers;

import com.aug.ecommerce.adapters.rest.dtos.RealizarOrdenRequestDTO;
import com.aug.ecommerce.application.commands.RealizarOrdenCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrdenMapper {
    RealizarOrdenCommand toCommand(RealizarOrdenRequestDTO dto);
    RealizarOrdenCommand.Item toItem(RealizarOrdenRequestDTO.ItemOrdenDTO dto);
}
