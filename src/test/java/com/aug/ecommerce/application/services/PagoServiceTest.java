package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.dtos.ResultadoPagoDTO;
import com.aug.ecommerce.application.events.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.events.PagoConfirmadoEvent;
import com.aug.ecommerce.application.gateways.PasarelaPagoClient;
import com.aug.ecommerce.application.publishers.PagoEventPublisher;
import com.aug.ecommerce.domain.models.pago.Pago;
import com.aug.ecommerce.domain.repositories.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para PagoService (implementación real):
 *  - Éxito: confirmar pago, guardar, publicar evento exitoso.
 *  - Error negocio: fallar pago, guardar, publicar evento no exitoso.
 *  - Timeout pasarela: tratar como fallo y publicar evento no exitoso con el mensaje de timeout.
 *
 * No se valida la secuencia; solo interacciones, conteo de persistencias y contenido del evento.
 */
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    PagoRepository pagoRepository;

    @Mock
    PasarelaPagoClient pasarelaPagoClient;

    @Mock
    PagoEventPublisher pagoEventPublisher;

    @InjectMocks
    PagoService pagoService;

    @Captor
    ArgumentCaptor<PagoConfirmadoEvent> eventoCaptor;

    /**
     * Caso feliz: la pasarela devuelve exitoso=true.
     * Debe confirmar el pago, persistir (dos veces) y publicar evento exitoso con codigoTransaccion.
     */
    @Test
    void realizarPago_exitoso_debeConfirmarGuardarYPublicar() throws Exception {
        OrdenPreparadaParaPagoEvent event = new OrdenPreparadaParaPagoEvent(99L, 123.45, "Tarjeta");

        try (MockedStatic<Pago> mockedPago = mockStatic(Pago.class)) {
            Pago pago = mock(Pago.class);
            when(pago.getId()).thenReturn(777L);

            mockedPago.when(() -> Pago.create(99L, 123.45, "Tarjeta")).thenReturn(pago);
            when(pagoRepository.save(pago)).thenReturn(pago);

            when(pasarelaPagoClient.realizarPago(pago))
                    .thenReturn(new ResultadoPagoDTO(true, "TXN-1", "OK"));

            // Act
            pagoService.realizarPago(event);

            // Assert
            verify(pagoRepository, times(2)).save(pago); // guardado inicial + guardado final
            verify(pasarelaPagoClient).realizarPago(pago);
            verify(pago).confirmar("TXN-1");
            verify(pago, never()).fallar(anyString());

            verify(pagoEventPublisher).publicarPagoRealizado(eventoCaptor.capture());
            PagoConfirmadoEvent evt = eventoCaptor.getValue();
            assertThat(evt.ordenId()).isEqualTo(99L);
            assertThat(evt.pagoId()).isEqualTo(777L);
            assertThat(evt.exitoso()).isTrue();
            assertThat(evt.codigoTransaccion()).isEqualTo("TXN-1");
            assertThat(evt.mensajeError()).isEqualTo("OK"); // en tu DTO, 'mensaje()' se mapea aquí
            assertThat(evt.fecha()).isBeforeOrEqualTo(Instant.now());
        }
    }

    /**
     * Caso de error de negocio: la pasarela devuelve exitoso=false y un mensaje.
     * Debe fallar el pago, persistir (dos veces) y publicar evento no exitoso con el mensaje.
     */
    @Test
    void realizarPago_error_debeFallarGuardarYPublicar() throws Exception {
        OrdenPreparadaParaPagoEvent event = new OrdenPreparadaParaPagoEvent(15L, 50.0, "Paypal");

        try (MockedStatic<Pago> mockedPago = mockStatic(Pago.class)) {
            Pago pago = mock(Pago.class);
            when(pago.getId()).thenReturn(123L);

            mockedPago.when(() -> Pago.create(15L, 50.0, "Paypal")).thenReturn(pago);
            when(pagoRepository.save(pago)).thenReturn(pago);

            final String MENSAJE = "Fondos insuficientes";
            when(pasarelaPagoClient.realizarPago(pago))
                    .thenReturn(new ResultadoPagoDTO(false, null, MENSAJE));

            // Act
            pagoService.realizarPago(event);

            // Assert
            verify(pagoRepository, times(2)).save(pago);
            verify(pasarelaPagoClient).realizarPago(pago);
            verify(pago).fallar(MENSAJE);
            verify(pago, never()).confirmar(anyString());

            verify(pagoEventPublisher).publicarPagoRealizado(eventoCaptor.capture());
            PagoConfirmadoEvent evt = eventoCaptor.getValue();
            assertThat(evt.ordenId()).isEqualTo(15L);
            assertThat(evt.pagoId()).isEqualTo(123L);
            assertThat(evt.exitoso()).isFalse();
            assertThat(evt.codigoTransaccion()).isNull();
            assertThat(evt.mensajeError()).isEqualTo(MENSAJE);
        }
    }

    /**
     * Caso de timeout de la pasarela: se captura TimeoutException y se construye
     * ResultadoPagoDTO(false, null, e.getMessage()).
     * Debe fallar el pago con el mensaje del timeout y publicar evento no exitoso.
     */
    @Test
    void realizarPago_timeout_debeFallarConMensajeDeTimeoutYPublicar() throws Exception {
        OrdenPreparadaParaPagoEvent event = new OrdenPreparadaParaPagoEvent(7L, 10.0, "Tarjeta");

        try (MockedStatic<Pago> mockedPago = mockStatic(Pago.class)) {
            Pago pago = mock(Pago.class);
            when(pago.getId()).thenReturn(9L);

            mockedPago.when(() -> Pago.create(7L, 10.0, "Tarjeta")).thenReturn(pago);
            when(pagoRepository.save(pago)).thenReturn(pago);

            when(pasarelaPagoClient.realizarPago(pago))
                    .thenThrow(new TimeoutException("Timeout en pasarela"));

            // Act
            pagoService.realizarPago(event);

            // Assert
            verify(pagoRepository, times(2)).save(pago);
            verify(pasarelaPagoClient).realizarPago(pago);
            verify(pago).fallar("Timeout en pasarela");
            verify(pago, never()).confirmar(anyString());

            verify(pagoEventPublisher).publicarPagoRealizado(eventoCaptor.capture());
            PagoConfirmadoEvent evt = eventoCaptor.getValue();
            assertThat(evt.ordenId()).isEqualTo(7L);
            assertThat(evt.pagoId()).isEqualTo(9L);
            assertThat(evt.exitoso()).isFalse();
            assertThat(evt.codigoTransaccion()).isNull();
            assertThat(evt.mensajeError()).isEqualTo("Timeout en pasarela");
        }
    }
}
