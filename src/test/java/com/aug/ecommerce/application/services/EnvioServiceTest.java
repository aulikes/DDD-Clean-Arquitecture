package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.dtos.ResultadoEnvioDTO;
import com.aug.ecommerce.application.events.EnvioPreparadoEvent;
import com.aug.ecommerce.application.gateways.ProveedorEnvioClient;
import com.aug.ecommerce.application.publishers.EnvioEventPublisher;
import com.aug.ecommerce.domain.models.envio.Envio;
import com.aug.ecommerce.domain.models.envio.EstadoEnvio;
import com.aug.ecommerce.domain.repositories.EnvioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Estas pruebas unitarias validan el comportamiento de EnvioService con inyección de mocks:
 *  - crearEnvio(): éxito y fallo (< tope de reintentos).
 *  - reintentarEnvios(): éxito, fallo < tope y fallo alcanzando el tope.
 */
@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    EnvioRepository envioRepository;

    @Mock
    ProveedorEnvioClient proveedorEnvioClient;

    @Mock
    EnvioEventPublisher envioEventPublisher;

    @InjectMocks
    EnvioService envioService;

    @Captor
    ArgumentCaptor<EnvioPreparadoEvent> eventoCaptor;

    /**
     * crearEnvio(): el proveedor responde exitoso.
     * Debe invocar iniciarPreparacionEnvio, persistir y publicar EnvioPreparadoEvent exitoso.
     */
    @Test
    void crearEnvio_exitoso_debePrepararPersistirYPublicar() {
        try (MockedStatic<Envio> mocked = mockStatic(Envio.class)) {
            Envio envioSpy = mock(Envio.class);
            when(envioSpy.getId()).thenReturn(42L);
            when(envioSpy.getOrdenId()).thenReturn(99L);

            mocked.when(() -> Envio.create(99L, "Calle 1")).thenReturn(envioSpy);

            // 1er guardado (crearEnvio) y 2º guardado (realizarEnvio)
            when(envioRepository.saveWithHistorial(envioSpy)).thenReturn(envioSpy);

            when(proveedorEnvioClient.prepararEnvio(envioSpy))
                    .thenReturn(new ResultadoEnvioDTO(true, "TRK-1", "OK", "Preparado"));

            // Act
            Envio out = envioService.crearEnvio(99L, "Calle 1");

            // Assert: orden real de invocaciones
            InOrder inOrder = inOrder(envioRepository, proveedorEnvioClient, envioSpy, envioEventPublisher);
            inOrder.verify(envioRepository).saveWithHistorial(envioSpy);              // 1) guardar inicial
            inOrder.verify(proveedorEnvioClient).prepararEnvio(envioSpy);            // 2) llamar proveedor
            inOrder.verify(envioSpy).incrementarReintentos();                        // 3) contar intento
            inOrder.verify(envioSpy).iniciarPreparacionEnvio("TRK-1");               // 4) éxito
            inOrder.verify(envioRepository).saveWithHistorial(envioSpy);             // 5) guardar con historial
            inOrder.verify(envioEventPublisher).publicarEnvioPreparado(eventoCaptor.capture()); // 6) publicar

            // Verificaciones adicionales
            verify(envioRepository, times(2)).saveWithHistorial(envioSpy);
            EnvioPreparadoEvent evt = eventoCaptor.getValue();
            assertThat(evt.ordenId()).isEqualTo(99L);
            assertThat(evt.envioId()).isEqualTo(42L);
            assertThat(evt.exitoso()).isTrue();
            assertThat(out).isSameAs(envioSpy);
        }
    }

    /**
     * crearEnvio(): el proveedor falla y, tras incrementar intentos, sigue por debajo del tope.
     * Debe invocar marcarErrorPendiente (lambda de crearEnvio), persistir y publicar evento no exitoso.
     */
    @Test
    void crearEnvio_falloBajoTope_debeMarcarErrorPendienteYPublicar() {
        try (MockedStatic<Envio> mocked = mockStatic(Envio.class)) {
            Envio envioMock = mock(Envio.class);
            when(envioMock.getId()).thenReturn(7L);
            when(envioMock.getOrdenId()).thenReturn(15L);

            // intentos: 0 -> tras incrementar = 1 (< 3) => ejecuta lambda onError
            java.util.concurrent.atomic.AtomicInteger intentos = new java.util.concurrent.atomic.AtomicInteger(0);
            when(envioMock.getIntentos()).thenAnswer(inv -> intentos.get());
            doAnswer(inv -> { intentos.incrementAndGet(); return null; }).when(envioMock).incrementarReintentos();

            mocked.when(() -> Envio.create(15L, "Dir X")).thenReturn(envioMock);
            when(envioRepository.saveWithHistorial(envioMock)).thenReturn(envioMock);

            // El servicio usa r.mensaje() tanto para log como para onError.accept(...)
            final String DETALLE = "Detalle opcional";
            final String MENSAJE = "Error X"; // ← este es el que usa mensaje()
            when(proveedorEnvioClient.prepararEnvio(envioMock))
                    .thenReturn(new ResultadoEnvioDTO(false, null, DETALLE, MENSAJE));

            // Act
            Envio out = envioService.crearEnvio(15L, "Dir X");

            // Assert: orden real de invocaciones
            InOrder inOrder = inOrder(envioRepository, proveedorEnvioClient, envioMock, envioEventPublisher);
            inOrder.verify(envioRepository).saveWithHistorial(envioMock);                 // 1) guardado inicial
            inOrder.verify(proveedorEnvioClient).prepararEnvio(envioMock);               // 2) proveedor
            inOrder.verify(envioMock).incrementarReintentos();                           // 3) intento++
            inOrder.verify(envioMock, never()).iniciarPreparacionEnvio(anyString());     // no éxito
            inOrder.verify(envioMock, never()).agregarEstadoFallido(anyString());        // no tope
            inOrder.verify(envioMock).marcarErrorPendiente(MENSAJE);                     // lambda onError
            inOrder.verify(envioRepository).saveWithHistorial(envioMock);                // 4) guardado final
            inOrder.verify(envioEventPublisher).publicarEnvioPreparado(eventoCaptor.capture()); // 5) publicar

            // Verificaciones adicionales
            verify(envioRepository, times(2)).saveWithHistorial(envioMock);
            EnvioPreparadoEvent evt = eventoCaptor.getValue();
            assertThat(evt.exitoso()).isFalse();
            assertThat(evt.mensajeError()).isEqualTo(MENSAJE);
            assertThat(out).isSameAs(envioMock);
        }
    }

    /**
     * reintentarEnvios(): el proveedor responde exitoso en el reintento.
     * Debe iniciar la preparación, persistir y publicar éxito.
     */
    @Test
    void reintentarEnvios_exitoso_debePrepararPersistirYPublicar() {
        Envio envioSpy = mock(Envio.class);
        when(envioSpy.getId()).thenReturn(300L);
        when(envioSpy.getOrdenId()).thenReturn(500L);

        AtomicInteger intentos = new AtomicInteger(0);
        doAnswer(inv -> { intentos.incrementAndGet(); return null; }).when(envioSpy).incrementarReintentos();

        try (MockedStatic<Envio> mockedEnvio = mockStatic(Envio.class)) {
            mockedEnvio.when(Envio::getEstadoInicial).thenReturn(EstadoEnvio.PENDIENTE);
            when(envioRepository.findByEstado(EstadoEnvio.PENDIENTE)).thenReturn(List.of(envioSpy));
        }

        when(proveedorEnvioClient.prepararEnvio(envioSpy))
                .thenReturn(new ResultadoEnvioDTO(true, "TRK-9", "OK", "Listo"));
        when(envioRepository.saveWithHistorial(envioSpy)).thenReturn(envioSpy);

        // Cuando
        envioService.reintentarEnvios();

        // Entonces
        verify(envioSpy).iniciarPreparacionEnvio("TRK-9");
        verify(envioRepository).saveWithHistorial(envioSpy);

        verify(envioEventPublisher).publicarEnvioPreparado(eventoCaptor.capture());
        EnvioPreparadoEvent evt = eventoCaptor.getValue();

        assertThat(evt.ordenId()).isEqualTo(500L);
        assertThat(evt.envioId()).isEqualTo(300L);
        assertThat(evt.exitoso()).isTrue();
    }

    /**
     * reintentarEnvios(): el proveedor falla y, tras incrementar, permanece por debajo del tope.
     * Debe invocar agregarEstadoPendiente (lambda de reintento), persistir y publicar evento no exitoso.
     */
    @Test
    void reintentarEnvios_falloBajoTope_debeAgregarEstadoPendiente() {
        Envio envioSpy = mock(Envio.class);
        when(envioSpy.getId()).thenReturn(1L);
        when(envioSpy.getOrdenId()).thenReturn(2L);

        AtomicInteger intentos = new AtomicInteger(1); // quedará en 2 tras incrementar (< 3)
        when(envioSpy.getIntentos()).thenAnswer(inv -> intentos.get());
        doAnswer(inv -> { intentos.incrementAndGet(); return null; }).when(envioSpy).incrementarReintentos();

        try (MockedStatic<Envio> mockedEnvio = mockStatic(Envio.class)) {
            mockedEnvio.when(Envio::getEstadoInicial).thenReturn(EstadoEnvio.PENDIENTE);
            when(envioRepository.findByEstado(EstadoEnvio.PENDIENTE)).thenReturn(List.of(envioSpy));
        }

        when(proveedorEnvioClient.prepararEnvio(envioSpy))
                .thenReturn(new ResultadoEnvioDTO(false, null, "Proveedor caído", "Temporal"));
        when(envioRepository.saveWithHistorial(envioSpy)).thenReturn(envioSpy);

        // Cuando
        envioService.reintentarEnvios();

        // Entonces
        verify(envioSpy).agregarEstadoPendiente("Temporal");
        verify(envioSpy, never()).agregarEstadoFallido(anyString());
        verify(envioRepository).saveWithHistorial(envioSpy);
        verify(envioEventPublisher).publicarEnvioPreparado(any(EnvioPreparadoEvent.class));
    }

    /**
     * reintentarEnvios(): el proveedor falla y, tras incrementar, se alcanza el tope (3).
     * Debe invocar agregarEstadoFallido y no agregar estado pendiente.
     */
    @Test
    void reintentarEnvios_falloAlcanzandoTope_debeMarcarFallido() {
        Envio envioSpy = mock(Envio.class);
        when(envioSpy.getId()).thenReturn(11L);
        when(envioSpy.getOrdenId()).thenReturn(22L);

        AtomicInteger intentos = new AtomicInteger(2); // tras incrementar = 3 (tope)
        when(envioSpy.getIntentos()).thenAnswer(inv -> intentos.get());
        doAnswer(inv -> { intentos.incrementAndGet(); return null; }).when(envioSpy).incrementarReintentos();

        try (MockedStatic<Envio> mockedEnvio = mockStatic(Envio.class)) {
            mockedEnvio.when(Envio::getEstadoInicial).thenReturn(EstadoEnvio.PENDIENTE);
            when(envioRepository.findByEstado(EstadoEnvio.PENDIENTE)).thenReturn(List.of(envioSpy));
        }

        when(proveedorEnvioClient.prepararEnvio(envioSpy))
                .thenReturn(new ResultadoEnvioDTO(false, null, "Sin respuesta", "Timeout"));
        when(envioRepository.saveWithHistorial(envioSpy)).thenReturn(envioSpy);

        // Cuando
        envioService.reintentarEnvios();

        // Entonces
        verify(envioSpy).agregarEstadoFallido("Excedido número máximo de reintentos.");
        verify(envioSpy, never()).agregarEstadoPendiente(anyString());
        verify(envioRepository).saveWithHistorial(envioSpy);
        verify(envioEventPublisher).publicarEnvioPreparado(any(EnvioPreparadoEvent.class));
    }

    @Test
    void crearEnvio_falloAlcanzandoTope_enPrimerIntento_debeMarcarFallidoYNoMarcarPendiente() {
        try (MockedStatic<Envio> mocked = mockStatic(Envio.class)) {
            Envio envio = mock(Envio.class);
            when(envio.getId()).thenReturn(70L);
            when(envio.getOrdenId()).thenReturn(700L);

            // Arranca en 2 -> tras incrementar = 3 (tope)
            java.util.concurrent.atomic.AtomicInteger intentos = new java.util.concurrent.atomic.AtomicInteger(2);
            when(envio.getIntentos()).thenAnswer(inv -> intentos.get());
            doAnswer(inv -> { intentos.incrementAndGet(); return null; }).when(envio).incrementarReintentos();

            mocked.when(() -> Envio.create(700L, "Dir Y")).thenReturn(envio);
            when(envioRepository.saveWithHistorial(envio)).thenReturn(envio);

            // Falla del proveedor (mensaje en 4º arg)
            when(proveedorEnvioClient.prepararEnvio(envio))
                    .thenReturn(new ResultadoEnvioDTO(false, null, "Detalle", "Cualquier error"));

            // Act
            envioService.crearEnvio(700L, "Dir Y");

            // Assert: al llegar a tope marca fallido y NO llama marcarErrorPendiente
            InOrder inOrder = inOrder(envioRepository, proveedorEnvioClient, envio, envioEventPublisher);
            inOrder.verify(envioRepository).saveWithHistorial(envio);
            inOrder.verify(proveedorEnvioClient).prepararEnvio(envio);
            inOrder.verify(envio).incrementarReintentos();
            inOrder.verify(envio, never()).iniciarPreparacionEnvio(anyString());
            inOrder.verify(envio).agregarEstadoFallido("Excedido número máximo de reintentos.");
            inOrder.verify(envio, never()).marcarErrorPendiente(anyString());
            inOrder.verify(envioRepository).saveWithHistorial(envio);
            inOrder.verify(envioEventPublisher).publicarEnvioPreparado(any(EnvioPreparadoEvent.class));
        }
    }

}
