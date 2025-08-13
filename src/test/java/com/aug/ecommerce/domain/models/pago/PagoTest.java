package com.aug.ecommerce.domain.models.pago;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PagoTest {

    @Test
    @DisplayName("create() inicia en PENDIENTE y confirmar/fallar respetan reglas")
    void crearYTransiciones() {
        var pago = Pago.create(77L, 250.0, "tarjeta");
        assertEquals(EstadoPago.PENDIENTE, pago.getEstado());

        pago.confirmar("trx-001");
        assertEquals(EstadoPago.CONFIRMADO, pago.getEstado());
        assertEquals("trx-001", pago.getCodigoTransaccion());

        // No debe permitir reprocesar
        assertThrows(IllegalStateException.class, () -> pago.confirmar("otra"));
        assertThrows(IllegalStateException.class, () -> pago.fallar("x"));
    }

    @Test
    @DisplayName("fallar() desde PENDIENTE cambia a FALLIDO con mensaje")
    void fallarDesdePendiente() {
        var pago = Pago.create(88L, 100.0, "paypal");
        pago.fallar("rechazado");
        assertEquals(EstadoPago.FALLIDO, pago.getEstado());
        assertEquals("rechazado", pago.getMensajeError());
    }
}
