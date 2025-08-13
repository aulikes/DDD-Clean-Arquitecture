package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.models.envio.Envio;
import com.aug.ecommerce.domain.models.envio.EnvioEstadoHistorial;
import com.aug.ecommerce.domain.models.envio.EstadoEnvio;
import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEstadoHistorialEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.enums.EstadoEnvioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Cobertura para EnvioMapper.toEntityWithHistorial(Envio).
 * Se mockea el dominio para centrarse en la lógica de mapeo.
 */
@ExtendWith(MockitoExtension.class)
class EnvioMapperWithHistorialTest {

    @Test
    @DisplayName("toEntityWithHistorial: mapea historial y setea relación inversa")
    void toEntityWithHistorial_mapeaHistorialYRelacionInversa() {
        // -------- Arrange (dominio mockeado) --------
        Envio envio = mock(Envio.class);

        // Campos base del Envío
        when(envio.getId()).thenReturn(5001L);
        when(envio.getOrdenId()).thenReturn(1001L);
        when(envio.getDireccionEnvio()).thenReturn("CL 1 # 2-3");
        when(envio.getEstado()).thenReturn(EstadoEnvio.DESPACHADO);
        when(envio.getTrackingNumber()).thenReturn("TRK-1");
        when(envio.getIntentos()).thenReturn(2);
        when(envio.getRazonFallo()).thenReturn(null);

        // Historial (2 entradas)
        EnvioEstadoHistorial h1 = mock(EnvioEstadoHistorial.class);
        when(h1.getId()).thenReturn(10L);
        when(h1.getEstadoEnvio()).thenReturn(EstadoEnvio.PREPARANDO);
        when(h1.getObservacion()).thenReturn("ok-1");
        when(h1.getFechaCambio()).thenReturn(Instant.parse("2024-01-01T00:00:00Z"));

        EnvioEstadoHistorial h2 = mock(EnvioEstadoHistorial.class);
        when(h2.getId()).thenReturn(11L);
        when(h2.getEstadoEnvio()).thenReturn(EstadoEnvio.DESPACHADO);
        when(h2.getObservacion()).thenReturn("ok-2");
        when(h2.getFechaCambio()).thenReturn(Instant.parse("2024-01-02T00:00:00Z"));

        when(envio.getHistorial()).thenReturn(List.of(h1, h2));

        // -------- Act --------
        EnvioEntity entity = EnvioMapper.toEntityWithHistorial(envio);

        // -------- Assert: campos base --------
        assertEquals(5001L, entity.getId());
        assertEquals(1001L, entity.getOrdenId());
        assertEquals("CL 1 # 2-3", entity.getDireccionEnvio());
        assertEquals(EstadoEnvioEntity.DESPACHADO, entity.getEstado());
        assertEquals("TRK-1", entity.getTrackingNumber());
        assertEquals(2, entity.getIntentos());
        assertNull(entity.getRazonFallo());

        // -------- Assert: historial mapeado + relación inversa --------
        List<EnvioEstadoHistorialEntity> he = entity.getHistorial();
        assertNotNull(he, "El historial de la entidad no debe ser nulo");
        assertEquals(2, he.size(), "Debe mapear todas las entradas del historial");

        EnvioEstadoHistorialEntity e1 = he.get(0);
        assertEquals(10L, e1.getId());
        assertEquals(EstadoEnvioEntity.PREPARANDO, e1.getEstado());
        assertEquals("ok-1", e1.getObservacion());
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), e1.getFechaCambio());
        assertSame(entity, e1.getEnvio(), "Debe establecer relación inversa hacia EnvioEntity");

        EnvioEstadoHistorialEntity e2 = he.get(1);
        assertEquals(11L, e2.getId());
        assertEquals(EstadoEnvioEntity.DESPACHADO, e2.getEstado());
        assertEquals("ok-2", e2.getObservacion());
        assertEquals(Instant.parse("2024-01-02T00:00:00Z"), e2.getFechaCambio());
        assertSame(entity, e2.getEnvio(), "Debe establecer relación inversa hacia EnvioEntity");
    }

    @Test
    @DisplayName("toEntityWithHistorial: historial vacío produce lista vacía (no nula)")
    void toEntityWithHistorial_historialVacio() {
        Envio envio = mock(Envio.class);
        when(envio.getId()).thenReturn(1L);
        when(envio.getOrdenId()).thenReturn(2L);
        when(envio.getDireccionEnvio()).thenReturn("X");
        when(envio.getEstado()).thenReturn(EstadoEnvio.PREPARANDO);
        when(envio.getTrackingNumber()).thenReturn(null);
        when(envio.getIntentos()).thenReturn(0);
        when(envio.getRazonFallo()).thenReturn(null);
        when(envio.getHistorial()).thenReturn(List.of());

        EnvioEntity entity = EnvioMapper.toEntityWithHistorial(envio);

        assertNotNull(entity.getHistorial(), "Debe setear lista (aunque vacía)");
        assertTrue(entity.getHistorial().isEmpty(), "La lista de historial debe quedar vacía");
    }
}
