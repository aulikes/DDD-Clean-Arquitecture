package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.events.EnvioPreparadoEvent;
import com.aug.ecommerce.application.events.PagoConfirmadoEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para OrdenValidacionService
 */
@ExtendWith(MockitoExtension.class)
class OrdenValidacionServiceTest {

    @Mock
    OrdenService ordenService;

    @InjectMocks
    OrdenValidacionService ordenValidacionService;

    /**
     * Debe NO marcar validada si aún faltan validaciones.
     */
    @Test
    void registrarValidacionExitosa_noMarcaOrden_siFaltanValidaciones() {
        Long ordenId = 100L;

        // Se registra solo CLIENTE
        ordenValidacionService.registrarValidacionExitosa(ordenId, ValidacionCrearOrden.CLIENTE);

        // Aún no se deben completar todas las validaciones requeridas
        verify(ordenService, never()).marcarOrdenValidada(anyLong());

        // Se añade PRODUCTO, sigue faltando STOCK
        ordenValidacionService.registrarValidacionExitosa(ordenId, ValidacionCrearOrden.PRODUCTO);

        verify(ordenService, never()).marcarOrdenValidada(anyLong());
    }

    /**
     * Debe marcar la orden como validada cuando se completan CLIENTE, PRODUCTO y STOCK (en cualquier orden).
     */
    @Test
    void registrarValidacionExitosa_marcaOrdenValidada_cuandoSeCompletaElConjunto() {
        Long ordenId = 101L;

        // Registro en orden arbitrario: PRODUCTO, STOCK, CLIENTE
        ordenValidacionService.registrarValidacionExitosa(ordenId, ValidacionCrearOrden.PRODUCTO);
        verify(ordenService, never()).marcarOrdenValidada(anyLong());

        ordenValidacionService.registrarValidacionExitosa(ordenId, ValidacionCrearOrden.STOCK);
        verify(ordenService, never()).marcarOrdenValidada(anyLong());

        ordenValidacionService.registrarValidacionExitosa(ordenId, ValidacionCrearOrden.CLIENTE);

        // Ahora sí debe marcarse como validada exactamente una vez
        verify(ordenService, times(1)).marcarOrdenValidada(ordenId);

        // Validación adicional: si se vuelve a registrar el mismo tipo, no debe volver a marcar (estado ya limpiado)
        ordenValidacionService.registrarValidacionExitosa(ordenId, ValidacionCrearOrden.CLIENTE);
        verifyNoMoreInteractions(ordenService);
    }

    /**
     * Debe marcar la orden como fallida con un mensaje que incluye el tipo de validación fallida.
     */
    @Test
    void registrarValidacionFallida_marcaOrdenFallida_conTipoEnMensaje() {
        Long ordenId = 200L;

        ordenValidacionService.registrarValidacionFallida(ordenId, ValidacionCrearOrden.STOCK);

        verify(ordenService).marcarOrdenFallida(eq(ordenId), contains("STOCK"));
        verifyNoMoreInteractions(ordenService);
    }

    /**
     * Gestionar pago: cuando exitoso=true, debe invocar pagoConfirmado.
     */
    @Test
    void gestionarInformacionPago_invocaPagoConfirmado_siExitoso() {
        Long ordenId = 300L;
        var event = new PagoConfirmadoEvent(
                ordenId,      // ordenId
                900L,         // pagoId
                Instant.now(),// fecha
                true,         // exitoso
                "TXN-1",      // codigoTransaccion
                null          // mensajeError
        );

        ordenValidacionService.gestionarInformacionPago(event);

        verify(ordenService).pagoConfirmado(ordenId);
        verify(ordenService, never()).pagoRechazado(anyLong());
    }

    /**
     * Gestionar pago: cuando exitoso=false, debe invocar pagoRechazado.
     */
    @Test
    void gestionarInformacionPago_invocaPagoRechazado_siNoExitoso() {
        Long ordenId = 301L;
        var event = new PagoConfirmadoEvent(
                ordenId, 901L, Instant.now(), false, null, "ERR"
        );

        ordenValidacionService.gestionarInformacionPago(event);

        verify(ordenService).pagoRechazado(ordenId);
        verify(ordenService, never()).pagoConfirmado(anyLong());
    }

    /**
     * Gestionar envío: cuando exitoso=true, debe invocar envioConfirmado.
     */
    @Test
    void gestionarInformacionEnvio_invocaEnvioConfirmado_siExitoso() {
        Long ordenId = 400L;
        var event = new EnvioPreparadoEvent(
                ordenId,      // ordenId
                800L,         // envioId
                Instant.now(),// fecha
                true,         // exitoso
                "TRACK-1",    // codigoTransaccion
                null          // mensajeError
        );

        ordenValidacionService.gestionarInformacionEnvio(event);

        verify(ordenService).envioConfirmado(ordenId);
        verify(ordenService, never()).envioError(anyLong());
    }

    /**
     * Gestionar envío: cuando exitoso=false, debe invocar envioError.
     */
    @Test
    void gestionarInformacionEnvio_invocaEnvioError_siNoExitoso() {
        Long ordenId = 401L;
        var event = new EnvioPreparadoEvent(
                ordenId, 801L, Instant.now(), false, null, "ERR"
        );

        ordenValidacionService.gestionarInformacionEnvio(event);

        verify(ordenService).envioError(ordenId);
        verify(ordenService, never()).envioConfirmado(anyLong());
    }
}
