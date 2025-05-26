package com.aug.ecommerce.adapters.rest.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RealizarOrdenRequestDTO {
    @NotNull
    private UUID clienteId;

    @NotEmpty
    private List<ItemOrdenDTO> items;

    @Data
    public static class ItemOrdenDTO {
        @NotNull
        private UUID productoId;

        @Min(1)
        private int cantidad;
    }
}
