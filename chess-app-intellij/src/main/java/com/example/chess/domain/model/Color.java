package com.example.chess.domain.model;

public enum Color {
    BLANCO, NEGRO;

    public Color oponente() {
        return this == BLANCO ? NEGRO : BLANCO;
    }
}