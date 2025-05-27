package com.aug.ecommerce.application.command;

import lombok.Data;

import java.util.List;

@Data
public class CrearClienteCommand {

    private final Long id;
    private String nombre;
    private String email;
    private final List<Direccion> direcciones;

    @Data
    public static class Direccion {
        private final Long id;
        private String calle;
        private String ciudad;
        private String pais;
        private String codigoPostal;

        public Direccion(Long id, String calle, String ciudad, String pais, String codigoPostal) {
            this.id = id;
            this.calle = calle;
            this.ciudad = ciudad;
            this.pais = pais;
            this.codigoPostal = codigoPostal;
        }
    }
}
