package com.aug.biblioteca.app.dominio;

public class Libro {

    private final String id;
    private String titulo;
    private String autor;
    private String editorial;
    private int anioPublicacion;
    private String genero;
    private boolean activo;

    public Libro(String id, String titulo, String autor, String isbn, String editorial, int anioPublicacion, String genero) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.genero = genero;
        this.activo = true;
    }

    public void editarDatos(String titulo, String autor, String editorial, int anioPublicacion, String genero) {
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.genero = genero;
    }

    public void desactivar() {
        this.activo = false;
    }

    public boolean estaActivo() {
        return activo;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getEditorial() {
        return editorial;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public String getGenero() {
        return genero;
    }
}

