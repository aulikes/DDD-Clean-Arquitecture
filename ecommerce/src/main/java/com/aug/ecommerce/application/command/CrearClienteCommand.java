package com.aug.ecommerce.application.command;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CrearClienteCommand {

    private final UUID id;
    private String nombre;
    private String email;
    private final List<Direccion> direcciones;

    @Data
    public static class Direccion {
        private final UUID id;
        private String calle;
        private String ciudad;
        private String pais;
        private String codigoPostal;

        public Direccion(UUID id, String calle, String ciudad, String pais, String codigoPostal) {
            this.id = id;
            this.calle = calle;
            this.ciudad = ciudad;
            this.pais = pais;
            this.codigoPostal = codigoPostal;
        }
    }
}
