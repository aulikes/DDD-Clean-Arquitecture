package com.example.chess.domain.model.piezas;

import com.example.chess.domain.model.Coordenada;
import com.example.chess.domain.model.Tablero;

public enum TipoPieza {
    TORRE {
        public boolean movimientoValido(Coordenada o, Coordenada d, Tablero t) {
            return esRecto(o, d) && t.caminoLibre(o, d);
        }
    },
    CABALLO {
        public boolean movimientoValido(Coordenada o, Coordenada d, Tablero t) {
            int df = Math.abs(o.getFila() - d.getFila());
            int dc = Math.abs(o.getColumna() - d.getColumna());
            return (df == 2 && dc == 1) || (df == 1 && dc == 2);
        }
    },
    ALFIL {
        public boolean movimientoValido(Coordenada o, Coordenada d, Tablero t) {
            return esDiagonal(o, d) && t.caminoLibre(o, d);
        }
    },
    REINA {
        public boolean movimientoValido(Coordenada o, Coordenada d, Tablero t) {
            return (esDiagonal(o, d) || esRecto(o, d)) && t.caminoLibre(o, d);
        }
    },
    REY {
        public boolean movimientoValido(Coordenada o, Coordenada d, Tablero t) {
            int df = Math.abs(o.getFila() - d.getFila());
            int dc = Math.abs(o.getColumna() - d.getColumna());
            return df <= 1 && dc <= 1;
        }
    },
    PEON {
        public boolean movimientoValido(Coordenada o, Coordenada d, Tablero t) {
            int direccion = o.getFila() < d.getFila() ? 1 : -1;
            boolean avanceUno = d.getColumna() == o.getColumna() && d.getFila() - o.getFila() == direccion;
            boolean avanceDos = (o.getFila() == 2 || o.getFila() == 7) && d.getColumna() == o.getColumna()
                    && d.getFila() - o.getFila() == 2 * direccion && t.estaLibreEntre(o, d);
            boolean capturaDiagonal = Math.abs(o.getColumna() - d.getColumna()) == 1
                    && d.getFila() - o.getFila() == direccion && t.hayPiezaEnemigaEn(o, d);
            return (avanceUno && t.estaLibre(d)) || avanceDos || capturaDiagonal;
        }
    };

    public abstract boolean movimientoValido(Coordenada origen, Coordenada destino, Tablero tablero);

    protected boolean esRecto(Coordenada o, Coordenada d) {
        return o.getFila() == d.getFila() || o.getColumna() == d.getColumna();
    }

    protected boolean esDiagonal(Coordenada o, Coordenada d) {
        return Math.abs(o.getFila() - d.getFila()) == Math.abs(o.getColumna() - d.getColumna());
    }
}