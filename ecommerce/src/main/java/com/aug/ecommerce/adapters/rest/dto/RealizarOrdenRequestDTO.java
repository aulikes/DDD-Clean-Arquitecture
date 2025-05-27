package com.aug.ecommerce.adapters.rest.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class RealizarOrdenRequestDTO {
    @NotNull
    private Long clienteId;

    @NotEmpty
    private List<ItemOrdenDTO> items;

    @Data
    public static class ItemOrdenDTO {
        @NotNull
        private Long productoId;

        @Min(1)
        private int cantidad;
    }
}
