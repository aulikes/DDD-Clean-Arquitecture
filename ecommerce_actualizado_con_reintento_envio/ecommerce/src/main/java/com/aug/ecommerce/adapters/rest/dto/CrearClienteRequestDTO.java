package com.aug.ecommerce.adapters.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CrearClienteRequestDTO {
    @NotNull
    private String nombre;

    @NotNull
    private String email;

    @NotEmpty
    private final List<Direccion> direcciones;

    @Data
    public static class Direccion {
        @NotNull
        private String calle;
        @NotNull
        private String ciudad;
        @NotNull
        private String pais;
        @NotNull
        private String codigoPostal;

        public Direccion(String calle, String ciudad, String pais, String codigoPostal) {
            this.calle = calle;
            this.ciudad = ciudad;
            this.pais = pais;
            this.codigoPostal = codigoPostal;
        }
    }
}
