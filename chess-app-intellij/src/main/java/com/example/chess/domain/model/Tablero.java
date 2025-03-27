package com.example.chess.domain.model;

import com.example.chess.domain.model.piezas.Pieza;
import com.example.chess.domain.model.piezas.TipoPieza;

import java.util.*;

public class Tablero {
    private final Map<Coordenada, Pieza> casillas = new HashMap<>();

    private Tablero() {}

    public static Tablero inicial() {
        Tablero t = new Tablero();
        t.casillas.put(new Coordenada('e', 1), new Pieza(TipoPieza.REY, Color.BLANCO));
        t.casillas.put(new Coordenada('e', 8), new Pieza(TipoPieza.REY, Color.NEGRO));
        t.casillas.put(new Coordenada('d', 8), new Pieza(TipoPieza.REINA, Color.BLANCO));
        return t;
    }

    public Pieza obtenerPieza(Coordenada coordenada) {
        return casillas.get(coordenada);
    }

    public void mover(Coordenada origen, Coordenada destino) {
        Pieza pieza = casillas.get(origen);
        if (pieza == null || !pieza.movimientoValido(origen, destino, this)) {
            throw new IllegalArgumentException("Movimiento inválido");
        }
        casillas.remove(origen);
        casillas.put(destino, pieza);
    }

    public boolean estaLibre(Coordenada c) {
        return !casillas.containsKey(c);
    }

    public boolean hayPiezaEnemigaEn(Coordenada origen, Coordenada destino) {
        Pieza p1 = casillas.get(origen);
        Pieza p2 = casillas.get(destino);
        return p1 != null && p2 != null && p1.getColor() != p2.getColor();
    }

    public boolean caminoLibre(Coordenada o, Coordenada d) {
        List<Coordenada> entre = obtenerCaminoEntre(o, d);
        for (Coordenada c : entre) {
            if (!estaLibre(c)) return false;
        }
        return true;
    }

    public boolean estaLibreEntre(Coordenada o, Coordenada d) {
        return caminoLibre(o, d);
    }

    public boolean estaEnJaque(Color color) {
        Coordenada rey = encontrarRey(color);
        if (rey == null) return false;

        for (Map.Entry<Coordenada, Pieza> entry : casillas.entrySet()) {
            Pieza p = entry.getValue();
            if (p.getColor() != color && p.movimientoValido(entry.getKey(), rey, this)) {
                return true;
            }
        }
        return false;
    }

    public boolean hayMovimientosLegales(Color color) {
        for (Map.Entry<Coordenada, Pieza> entry : casillas.entrySet()) {
            Coordenada origen = entry.getKey();
            Pieza pieza = entry.getValue();
            if (pieza.getColor() != color) continue;

            for (char col = 'a'; col <= 'h'; col++) {
                for (int fila = 1; fila <= 8; fila++) {
                    Coordenada destino = new Coordenada(col, fila);
                    if (pieza.movimientoValido(origen, destino, this)) {
                        Tablero copia = this.copiar();
                        try {
                            copia.mover(origen, destino);
                            if (!copia.estaEnJaque(color)) {
                                return true;
                            }
                        } catch (Exception e) {
                            // Movimiento no válido
                        }
                    }
                }
            }
        }
        return false;
    }

    private Coordenada encontrarRey(Color color) {
        for (Map.Entry<Coordenada, Pieza> entry : casillas.entrySet()) {
            if (entry.getValue().getTipo() == TipoPieza.REY && entry.getValue().getColor() == color) {
                return entry.getKey();
            }
        }
        return null;
    }

    private List<Coordenada> obtenerCaminoEntre(Coordenada o, Coordenada d) {
        List<Coordenada> camino = new ArrayList<>();
        int df = Integer.compare(d.getFila(), o.getFila());
        int dc = Integer.compare(d.getColumna(), o.getColumna());

        char col = (char) (o.getColumna() + dc);
        int fila = o.getFila() + df;

        while (col != d.getColumna() || fila != d.getFila()) {
            camino.add(new Coordenada(col, fila));
            col += dc;
            fila += df;
        }
        return camino;
    }

    public Map<Coordenada, Pieza> getCasillas() {
        return Collections.unmodifiableMap(casillas);
    }

    public Tablero copiar() {
        Tablero copia = new Tablero();
        for (Map.Entry<Coordenada, Pieza> entry : casillas.entrySet()) {
            copia.casillas.put(entry.getKey(), entry.getValue());
        }
        return copia;
    }
}