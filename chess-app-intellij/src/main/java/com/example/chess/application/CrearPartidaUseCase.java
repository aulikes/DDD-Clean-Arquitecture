package com.example.chess.application;

import com.example.chess.domain.model.Color;
import com.example.chess.domain.model.Jugador;
import com.example.chess.domain.model.PartidaDeAjedrez;

public class CrearPartidaUseCase {
    public PartidaDeAjedrez ejecutar(String nombreBlanco, String nombreNegro) {
        Jugador blanco = new Jugador(nombreBlanco, Color.BLANCO);
        Jugador negro = new Jugador(nombreNegro, Color.NEGRO);
        return new PartidaDeAjedrez(blanco, negro);
    }
}