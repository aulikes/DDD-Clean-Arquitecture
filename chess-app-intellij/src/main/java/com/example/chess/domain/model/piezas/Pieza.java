package com.example.chess.domain.model.piezas;

import com.example.chess.domain.model.Color;
import com.example.chess.domain.model.Coordenada;
import com.example.chess.domain.model.Tablero;

public class Pieza {
    private final TipoPieza tipo;
    private final Color color;

    public Pieza(TipoPieza tipo, Color color) {
        this.tipo = tipo;
        this.color = color;
    }

    public Color getColor() { return color; }
    public TipoPieza getTipo() { return tipo; }

    public boolean movimientoValido(Coordenada origen, Coordenada destino, Tablero tablero) {
        return tipo.movimientoValido(origen, destino, tablero);
    }
}