package com.aug.ecommerce.domain.models.envio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EnvioTest {

    @Test
    @DisplayName("create() inicia en estado inicial y sin tracking")
    void crearEnvio() {
        // Se verifica el estado inicial del agregado Envio al crearse
        var envio = Envio.create(55L, "CL 99 # 9-99");
        assertEquals(55L, envio.getOrdenId());
        assertEquals(Envio.getEstadoInicial(), envio.getEstado());
        assertNull(envio.getTrackingNumber());
        assertEquals(0, envio.getIntentos());
    }

    @Test
    @DisplayName("iniciarPreparacionEnvio falla si hay un estado abierto previo (invariante del historial)")
    void iniciarPreparacionConEstadoAbiertoLanzaExcepcion() {
        // Según la invariante del dominio: "Solo puede haber un estado en historial sin fecha de cambio".
        // Al crear un Envio, el estado inicial queda 'abierto' (sin fecha de cambio). Si se intenta agregar
        // otro estado (iniciarPreparacionEnvio) sin cerrar el anterior, debe lanzar IllegalStateException.
        var envio = Envio.create(1L, "addr");
        assertThrows(IllegalStateException.class, () -> envio.iniciarPreparacionEnvio("TRK-1"));
    }

    @Test
    @DisplayName("agregarEstadoFallido falla si hay un estado abierto previo (invariante del historial)")
    void estadoFallidoConEstadoAbiertoLanzaExcepcion() {
        // Igual que el caso anterior, se intenta agregar un nuevo estado sin cerrar el actual.
        var envio = Envio.create(1L, "addr");
        assertThrows(IllegalStateException.class, () -> envio.agregarEstadoFallido("no cobertura"));
    }
}
