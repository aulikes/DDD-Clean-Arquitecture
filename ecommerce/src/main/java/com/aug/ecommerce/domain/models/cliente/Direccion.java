package com.aug.ecommerce.domain.model.cliente;

import java.util.Objects;

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
        this.calle = Objects.requireNonNull(nuevaCalle, "La calle no puede ser null");
        this.ciudad = Objects.requireNonNull(nuevaCiudad, "La ciudad no puede ser null");
        this.pais = Objects.requireNonNull(nuevoPais, "El pais no puede ser null");
        this.codigoPostal = Objects.requireNonNull(nuevoCodigoPostal, "El codigoPostal no puede ser null");
    }
}
