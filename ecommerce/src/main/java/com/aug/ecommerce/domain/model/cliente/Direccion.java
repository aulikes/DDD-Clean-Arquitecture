package com.aug.ecommerce.domain.model.cliente;

import com.aug.ecommerce.domain.util.ValidadorDominio;

import java.util.Objects;
import java.util.UUID;

public final class Direccion {
    private final UUID id;
    private String calle;
    private String ciudad;
    private String pais;
    private String codigoPostal;

    // Constructor solo visible dentro del package
    Direccion(UUID id, String calle, String ciudad, String pais, String codigoPostal) {
        this.id = Objects.requireNonNull(id, "El id no puede ser nulo");
        actualizar(calle, ciudad, pais, codigoPostal);
    }

    public UUID getId() { return id; }
    public String getCalle() { return calle; }
    public String getCiudad() { return ciudad; }
    public String getPais() { return pais; }
    public String getCodigoPostal() { return codigoPostal; }

    // Solo modificable desde Cliente
    void actualizar(String nuevaCalle, String nuevaCiudad, String nuevoPais, String nuevoCodigoPostal) {
        this.calle = ValidadorDominio.validarCampoObligatorio(nuevaCalle);
        this.ciudad = ValidadorDominio.validarCampoObligatorio(nuevaCiudad);
        this.pais = ValidadorDominio.validarCampoObligatorio(nuevoPais);
        this.codigoPostal = ValidadorDominio.validarCampoObligatorio(nuevoCodigoPostal);
    }
}
