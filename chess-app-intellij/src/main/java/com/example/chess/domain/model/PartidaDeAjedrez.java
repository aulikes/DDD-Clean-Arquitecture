package com.example.chess.domain.model;

import com.example.chess.domain.model.piezas.Pieza;

import java.util.Map;
import java.util.UUID;

public class PartidaDeAjedrez {
    private final UUID id;
    private final Jugador jugadorBlanco;
    private final Jugador jugadorNegro;
    private final Tablero tablero;
    private Color turnoActual;
    private boolean finalizada;

    public PartidaDeAjedrez(Jugador blanco, Jugador negro) {
        this.id = UUID.randomUUID();
        this.jugadorBlanco = blanco;
        this.jugadorNegro = negro;
        this.tablero = Tablero.inicial();
        this.turnoActual = Color.BLANCO;
        this.finalizada = false;
    }

    public void mover(Coordenada origen, Coordenada destino) {
        if (finalizada) throw new IllegalStateException("La partida ya finalizó");

        Pieza pieza = tablero.obtenerPieza(origen);
        if (pieza == null)
            throw new IllegalArgumentException("No hay pieza en la casilla de origen");
        if (pieza.getColor() != turnoActual)
            throw new IllegalArgumentException("No es el turno del color de esta pieza");

        tablero.mover(origen, destino);

        if (estaEnJaque(turnoActual.oponente()) && estaEnJaqueMate(turnoActual.oponente())) {
            this.finalizada = true;
        }

        this.turnoActual = turnoActual.oponente();
    }

    public boolean estaEnJaque(Color color) {
        return tablero.estaEnJaque(color);
    }

    public boolean estaEnJaqueMate(Color color) {
        return estaEnJaque(color) && !tablero.hayMovimientosLegales(color);
    }

    public UUID getId() {
        return id;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public Color getTurnoActual() {
        return turnoActual;
    }

    public boolean isFinalizada() {
        return finalizada;
    }

    public void finalizar() {
        this.finalizada = true;
    }
}