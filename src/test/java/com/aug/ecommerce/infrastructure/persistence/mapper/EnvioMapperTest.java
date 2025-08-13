package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.models.envio.Envio;
import com.aug.ecommerce.domain.models.envio.EstadoEnvio;
import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.EnvioEstadoHistorialEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.enums.EstadoEnvioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para EnvioMapper.
 * Nota: toEntity NO propaga el historial; solo mapea campos simples.
 */
class EnvioMapperTest {

    @Test
    @DisplayName("toEntity: mapea campos simples y enum EstadoEnvio -> EstadoEnvioEntity (sin historial)")
    void toEntity_ok() {
        var envio = Envio.fromPersistence(
                5001L,              // id
                1001L,              // ordenId
                "CL 1 # 2-3",       // direccion
                EstadoEnvio.DESPACHADO,
                "TRK-1",            // tracking
                null,               // razonFallo
                2,                  // intentos
                List.of()           // historial (no se usa en toEntity)
        );

        EnvioEntity e = EnvioMapper.toEntity(envio);

        assertEquals(5001L, e.getId());
        assertEquals(1001L, e.getOrdenId());
        assertEquals("CL 1 # 2-3", e.getDireccionEnvio());
        assertEquals(EstadoEnvioEntity.DESPACHADO, e.getEstado());
        assertEquals("TRK-1", e.getTrackingNumber());
        assertEquals(2, e.getIntentos());
        assertNull(e.getRazonFallo());
        // Por diseño actual del mapper, el historial NO se mapea en toEntity:
        assertNull(e.getHistorial());
    }

    @Test
    @DisplayName("toDomainWithHistorial: reconstruye historial completo y enum Estado")
    void toDomainWithHistorial_ok() {
        var e = new EnvioEntity();
        e.setId(5002L);
        e.setOrdenId(1002L);
        e.setDireccionEnvio("CL 9 # 9-9");
        e.setEstado(EstadoEnvioEntity.PREPARANDO);
        e.setTrackingNumber("TRK-2");
        e.setIntentos(1);
        e.setRazonFallo(null);

        var h1 = new EnvioEstadoHistorialEntity();
        h1.setId(1L);
        h1.setEstado(EstadoEnvioEntity.PREPARANDO);
        h1.setFechaCambio(Instant.parse("2024-01-01T00:00:00Z"));
        h1.setObservacion("ok-1");

        var h2 = new EnvioEstadoHistorialEntity();
        h2.setId(2L);
        h2.setEstado(EstadoEnvioEntity.DESPACHADO);
        h2.setFechaCambio(Instant.parse("2024-01-02T00:00:00Z"));
        h2.setObservacion("ok-2");

        e.setHistorial(List.of(h1, h2));

        var d = EnvioMapper.toDomainWithHistorial(e);

        assertEquals(5002L, d.getId());
        assertEquals(1002L, d.getOrdenId());
        assertEquals("CL 9 # 9-9", d.getDireccionEnvio());
        assertEquals(EstadoEnvio.PREPARANDO, d.getEstado());
        assertEquals("TRK-2", d.getTrackingNumber());
        assertEquals(1, d.getIntentos());
        assertNull(d.getRazonFallo());

        assertEquals(2, d.getHistorial().size());
        assertEquals(EstadoEnvio.PREPARANDO, d.getHistorial().get(0).getEstadoEnvio());
        assertEquals("ok-1", d.getHistorial().get(0).getObservacion());
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), d.getHistorial().get(0).getFechaCambio());

        assertEquals(EstadoEnvio.DESPACHADO, d.getHistorial().get(1).getEstadoEnvio());
        assertEquals("ok-2", d.getHistorial().get(1).getObservacion());
        assertEquals(Instant.parse("2024-01-02T00:00:00Z"), d.getHistorial().get(1).getFechaCambio());
    }
}
