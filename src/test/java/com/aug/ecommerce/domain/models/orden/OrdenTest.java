package com.aug.ecommerce.domain.models.orden;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrdenTest {

    @Test
    @DisplayName("create() inicia en PENDIENTE_VALIDACION y sin ítems")
    void crearOrdenInicial() {
        // Se crea la orden en su estado inicial del dominio
        var orden = Orden.create(10L, "CL 1 # 2-3");

        assertEquals(0, orden.getItems().size(), "Debe iniciar sin ítems");
        assertEquals(
                EstadoOrden.deTipo(EstadoOrden.Tipo.PENDIENTE_VALIDACION),
                orden.getEstado(),
                "El estado inicial debe ser PENDIENTE_VALIDACION"
        );
    }

    @Test
    @DisplayName("Agregar ítems acumula el total correctamente")
    void agregarItemsYCalcularTotal() {
        // Se validan los cálculos del total sin depender de IDs (que podrían ser nulos en memoria)
        var orden = Orden.create(10L, "CL 1 # 2-3");

        orden.agregarItem(101L, 2, 50.0);  // subtotal 100.0
        orden.agregarItem(102L, 1, 20.0);  // subtotal  20.0  -> total 120.0

        assertEquals(120.0, orden.calcularTotal(), 0.0001, "La suma de subtotales debe ser correcta");
        assertEquals(2, orden.getItems().size(), "Deben existir dos ítems");
    }

    @Test
    @DisplayName("No permite ir de PENDIENTE_VALIDACION a PAGO_EN_PROCESO (regla de transición)")
    void transicionInvalidaAPagoEnProceso() {
        // Regla del dominio: no se puede transicionar directamente a PAGO_EN_PROCESO desde PENDIENTE_VALIDACION.
        var orden = Orden.create(10L, "CL 1 # 2-3");

        // En el estado inicial la orden es editable (agregar ítems no debe fallar)
        assertDoesNotThrow(() -> orden.agregarItem(1L, 1, 10.0),
                "En estado PENDIENTE_VALIDACION la orden debe ser editable para agregar ítems");

        // Al intentar iniciar pago sin pasar por los estados previos requeridos, debe lanzar IllegalStateException
        assertThrows(IllegalStateException.class, orden::iniciarPago,
                "No debe permitirse la transición directa a PAGO_EN_PROCESO");
    }
}
