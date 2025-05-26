package com.aug.ecommerce.domain.model.producto;

import com.aug.ecommerce.domain.util.ValidadorDominio;
import java.util.UUID;

public class ProductoDigital extends Producto {
    private String urlDescarga;

    public ProductoDigital(UUID id, String nombre, String descripcion, Double precio, String imagenUrl, String urlDescarga) {
        super(id, nombre, descripcion, precio, imagenUrl);
        this.setUrlDescarga(urlDescarga);
    }

    public String getUrlDescarga() { return urlDescarga; }

    public void setUrlDescarga(String urlDescarga) {
        this.urlDescarga = ValidadorDominio.validarCampoObligatorio(urlDescarga);
    }
}
