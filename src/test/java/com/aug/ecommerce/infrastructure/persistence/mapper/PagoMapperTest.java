package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.models.pago.EstadoPago;
import com.aug.ecommerce.domain.models.pago.Pago;
import com.aug.ecommerce.infrastructure.persistence.entity.PagoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para PagoMapper.
 * Verifica la conversión de enums y campos adicionales.
 */
class PagoMapperTest {

    @Test
    @DisplayName("toEntity: mapea estado (enum) y demás campos")
    void toEntity_ok() {
        var pago = Pago.fromPersistence(
                900L, 1001L, 250.0, "TARJETA",
                EstadoPago.CONFIRMADO, "TRX-1", null
        );

        PagoEntity e = PagoMapper.toEntity(pago);

        assertEquals(900L, e.getId());
        assertEquals(1001L, e.getOrdenId());
        assertEquals(250.0, e.getMonto());
        assertEquals("TARJETA", e.getMetodo());
        assertEquals(PagoEntity.Estado.CONFIRMADO, e.getEstado());
        assertEquals("TRX-1", e.getCodigoTransaccion());
        assertNull(e.getMensajeError());
    }

    @Test
    @DisplayName("toDomain: mapea estado (enum) y demás campos")
    void toDomain_ok() {
        var e = new PagoEntity();
        e.setId(901L);
        e.setOrdenId(2002L);
        e.setMonto(99.9);
        e.setMetodo("NEQUI");
        e.setEstado(PagoEntity.Estado.FALLIDO);
        e.setCodigoTransaccion("TRX-2");
        e.setMensajeError("Saldo insuficiente");

        Pago domain = PagoMapper.toDomain(e);

        assertEquals(901L, domain.getId());
        assertEquals(2002L, domain.getOrdenId());
        assertEquals(99.9, domain.getMonto());
        assertEquals("NEQUI", domain.getMetodo());
        assertEquals(EstadoPago.FALLIDO, domain.getEstado());
        assertEquals("TRX-2", domain.getCodigoTransaccion());
        assertEquals("Saldo insuficiente", domain.getMensajeError());
    }
}
