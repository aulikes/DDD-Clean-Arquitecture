package com.aug.ecommerce.domain.model.cliente;

import com.aug.ecommerce.domain.util.ValidadorDominio;

public final class Direccion {
    private final Long id;
    private String calle;
    private String ciudad;
    private String pais;
    private String codigoPostal;

    // Constructor solo visible dentro del package
    Direccion(Long id, String calle, String ciudad, String pais, String codigoPostal) {
        this.id = id;
        actualizar(calle, ciudad, pais, codigoPostal);
    }

    Direccion(String calle, String ciudad, String pais, String codigoPostal) {
        this.id = null;
        actualizar(calle, ciudad, pais, codigoPostal);
    }

    public Long getId() { return id; }
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
