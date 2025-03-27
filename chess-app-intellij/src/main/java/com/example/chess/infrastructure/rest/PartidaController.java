package com.example.chess.infrastructure.rest;

import com.example.chess.application.CrearPartidaUseCase;
import com.example.chess.domain.model.Coordenada;
import com.example.chess.domain.model.PartidaDeAjedrez;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/partidas")
public class PartidaController {
    private final CrearPartidaUseCase crearPartidaUseCase = new CrearPartidaUseCase();
    private final Map<UUID, PartidaDeAjedrez> repositorioEnMemoria = new ConcurrentHashMap<>();

    @PostMapping
    public UUID crearPartida(@RequestParam String jugadorBlanco, @RequestParam String jugadorNegro) {
        PartidaDeAjedrez partida = crearPartidaUseCase.ejecutar(jugadorBlanco, jugadorNegro);
        repositorioEnMemoria.put(partida.getId(), partida);
        return partida.getId();
    }

    @PutMapping("/{id}/mover")
    public String mover(@PathVariable UUID id,
                        @RequestParam char colOrigen, @RequestParam int filaOrigen,
                        @RequestParam char colDestino, @RequestParam int filaDestino) {
        PartidaDeAjedrez partida = repositorioEnMemoria.get(id);
        if (partida == null) return "Partida no encontrada";

        Coordenada origen = new Coordenada(colOrigen, filaOrigen);
        Coordenada destino = new Coordenada(colDestino, filaDestino);
        partida.mover(origen, destino);
        return "Movimiento realizado";
    }
}