package com.aug.ecommerce.adapters.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RealizarPagoRequestDTO {

    @NotNull
    private Long ordenId;

    @NotNull
    private String medioPago;
}
