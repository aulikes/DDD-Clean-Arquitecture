package com.aug.biblioteca.app.dominio;

public class Usuario {

    private final String id;
    private final String nombre;
    private final String correo;
    private boolean activo;

    public Usuario(String id, String nombre, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }

    public boolean estaActivo() {
        return this.activo;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }
}

