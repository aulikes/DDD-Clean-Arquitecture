package com.aug.ecommerce.adapters.rest.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrearProductoRequestDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @Positive
    private double precio;

    @NotBlank
    private String imagenUrl;

    @NotEmpty
    private Long cantidad;

    @NotEmpty
    private Long categoriaId;
}
