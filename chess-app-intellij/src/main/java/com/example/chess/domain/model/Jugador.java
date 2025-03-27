package com.example.chess.domain.model;

import java.util.UUID;

public class Jugador {
    private final UUID id;
    private final String nombre;
    private final Color color;

    public Jugador(String nombre, Color color) {
        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.color = color;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Color getColor() {
        return color;
    }
}