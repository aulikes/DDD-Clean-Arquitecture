package com.aug.ecommerce.domain.util;

public final class ValidadorDominio {
    private ValidadorDominio() {}

    public static String validarCampoObligatorio(String valor) {
        if (valor == null || valor.isBlank())
            throw new IllegalArgumentException("El campo no puede estar vacío");
        return valor;
    }
}