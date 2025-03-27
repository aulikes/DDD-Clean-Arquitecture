package com.example.chess.domain.model;

import java.util.Objects;

public class Coordenada {
    private final char columna;
    private final int fila;

    public Coordenada(char columna, int fila) {
        if (columna < 'a' || columna > 'h' || fila < 1 || fila > 8) {
            throw new IllegalArgumentException("Coordenada inválida: " + columna + fila);
        }
        this.columna = columna;
        this.fila = fila;
    }

    public char getColumna() { return columna; }
    public int getFila() { return fila; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordenada)) return false;
        Coordenada that = (Coordenada) o;
        return columna == that.columna && fila == that.fila;
    }

    @Override
    public int hashCode() {
        return Objects.hash(columna, fila);
    }

    @Override
    public String toString() {
        return "" + columna + fila;
    }
}