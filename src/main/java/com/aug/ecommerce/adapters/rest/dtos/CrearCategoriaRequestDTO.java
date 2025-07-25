package com.aug.ecommerce.adapters.rest.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CrearCategoriaRequestDTO {
    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;
}
