package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.commands.RealizarOrdenCommand;
import com.aug.ecommerce.application.commands.RealizarPagoCommand;
import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.events.OrdenPagadaEvent;
import com.aug.ecommerce.application.events.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.publishers.OrdenEventPublisher;
import com.aug.ecommerce.domain.models.orden.EstadoOrden;
import com.aug.ecommerce.domain.models.orden.Orden;
import com.aug.ecommerce.domain.repositories.OrdenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para OrdenService
 */
@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock
    OrdenRepository ordenRepository;

    @Mock
    OrdenEventPublisher publisher;

    @InjectMocks
    OrdenService ordenService;

    @Captor
    ArgumentCaptor<OrdenCreadaEvent> evtCreadaCaptor;
    @Captor
    ArgumentCaptor<OrdenPreparadaParaPagoEvent> evtPagoReqCaptor;
    @Captor
    ArgumentCaptor<OrdenPagadaEvent> evtEnvioReqCaptor;

    /**
     * crearOrden(): debe crear el agregado, agregar items, guardar, enviar a validación y publicar evento.
     */
    @Test
    void crearOrden_debeGuardarEnviarAValidacionYPublicar() {
        // Dado: command con 2 items
        var cmd = new RealizarOrdenCommand(
                99L,
                List.of(
                        new RealizarOrdenCommand.Item(1L, 2, 10.0),
                        new RealizarOrdenCommand.Item(2L, 1, 20.0)
                ),
                "Calle 1"
        );

        try (MockedStatic<Orden> mocked = mockStatic(Orden.class)) {
            Orden orden = mock(Orden.class);

            when(orden.getId()).thenReturn(123L);
            when(orden.getClienteId()).thenReturn(99L);
            when(orden.getDireccionEnviar()).thenReturn("Calle 1");
            when(orden.getItems()).thenReturn(List.of()); // evitar depender de ItemOrden real

            mocked.when(() -> Orden.create(99L, "Calle 1")).thenReturn(orden);
            when(ordenRepository.save(orden)).thenReturn(orden);

            // Cuando
            Long idGenerado = ordenService.crearOrden(cmd);

            // Entonces: se agregaron los items, se guardó y se publicó evento de creación
            verify(orden, times(1)).agregarItem(1L, 2, 10.0);
            verify(orden, times(1)).agregarItem(2L, 1, 20.0);
            verify(ordenRepository).save(orden);
            verify(orden).enviarAValidacion();

            verify(publisher).publishOrdenCreated(evtCreadaCaptor.capture());
            OrdenCreadaEvent evt = evtCreadaCaptor.getValue();
            assertThat(evt.ordenId()).isEqualTo(123L);
            assertThat(evt.clienteId()).isEqualTo(99L);
            assertThat(evt.direccion()).isEqualTo("Calle 1");
            // items puede ser vacío, no es relevante para este test

            assertThat(idGenerado).isEqualTo(123L);
        }
    }

    /**
     * marcarOrdenValidada(): pasa a LISTA_PARA_PAGO y guarda.
     */
    @Test
    void marcarOrdenValidada_debeMutarEstadoYGuardar() {
        Orden orden = mock(Orden.class);
        when(ordenRepository.findById(10L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(orden)).thenReturn(orden);

        ordenService.marcarOrdenValidada(10L);

        verify(ordenRepository).findById(10L);
        verify(orden).marcarListaParaPago();
        verify(ordenRepository).save(orden);
    }

    /**
     * marcarOrdenFallida(): si YA está en VALIDACION_FALLIDA, no hace nada.
     */
    @Test
    void marcarOrdenFallida_noHaceNada_siYaEstaFallida() {
        Orden orden = mock(Orden.class);

        // Simula que getEstado() == EstadoOrden.deTipo(VALIDACION_FALLIDA)
        try (MockedStatic<EstadoOrden> mockedEstado = mockStatic(EstadoOrden.class)) {
            EstadoOrden fallida = mock(EstadoOrden.class);
            mockedEstado.when(() -> EstadoOrden.deTipo(EstadoOrden.Tipo.VALIDACION_FALLIDA))
                    .thenReturn(fallida);
            when(ordenRepository.findById(11L)).thenReturn(Optional.of(orden));
            when(orden.getEstado()).thenReturn(fallida);

            ordenService.marcarOrdenFallida(11L, "err");

            verify(ordenRepository).findById(11L);
            verify(orden, never()).marcarValidacionFallida(anyString());
            verify(ordenRepository, never()).save(any());
        }
    }

    /**
     * marcarOrdenFallida(): si NO está en VALIDACION_FALLIDA, la marca como fallida y guarda.
     */
    @Test
    void marcarOrdenFallida_debeMarcarYGuardar_siNoEstaFallida() {
        Orden orden = mock(Orden.class);

        try (MockedStatic<EstadoOrden> mockedEstado = mockStatic(EstadoOrden.class)) {
            EstadoOrden fallida = mock(EstadoOrden.class);
            EstadoOrden otro = mock(EstadoOrden.class);
            mockedEstado.when(() -> EstadoOrden.deTipo(EstadoOrden.Tipo.VALIDACION_FALLIDA))
                    .thenReturn(fallida);

            when(ordenRepository.findById(12L)).thenReturn(Optional.of(orden));
            when(orden.getEstado()).thenReturn(otro); // distinto a VALIDACION_FALLIDA

            ordenService.marcarOrdenFallida(12L, "error X");

            verify(orden).marcarValidacionFallida("error X");
            verify(ordenRepository).save(orden);
        }
    }

    /**
     * reenviarOrdenAValidacion(): guarda, envía a validación y publica evento.
     */
    @Test
    void reenviarOrdenAValidacion_debeGuardarEnviarYPublicar() {
        Orden orden = mock(Orden.class);
        when(ordenRepository.findById(15L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(orden)).thenReturn(orden);

        // Para construir el evento sin NPE
        when(orden.getId()).thenReturn(15L);
        when(orden.getClienteId()).thenReturn(7L);
        when(orden.getDireccionEnviar()).thenReturn("Dir");
        when(orden.getItems()).thenReturn(List.of());

        ordenService.reenviarOrdenAValidacion(15L);

        verify(ordenRepository).findById(15L);
        verify(ordenRepository).save(orden);
        verify(orden).enviarAValidacion();
        verify(publisher).publishOrdenCreated(any(OrdenCreadaEvent.class));
    }

    /**
     * solicitarPago(): si existe la orden -> iniciarPago, guardar y publicar OrdenPreparadaParaPagoEvent.
     */
    @Test
    void solicitarPago_debePublicarPagoRequerido_siOrdenExiste() {
        Orden orden = mock(Orden.class);
        when(ordenRepository.findById(50L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(orden)).thenReturn(orden);

        when(orden.getId()).thenReturn(50L);
        when(orden.calcularTotal()).thenReturn(123.45);

        var cmd = new RealizarPagoCommand(50L, "Tarjeta");

        ordenService.solicitarPago(cmd);

        verify(orden).iniciarPago();
        verify(ordenRepository).save(orden);
        verify(publisher).publishOrdenPagoRequerido(evtPagoReqCaptor.capture());

        OrdenPreparadaParaPagoEvent evt = evtPagoReqCaptor.getValue();
        assertThat(evt.ordenId()).isEqualTo(50L);
        assertThat(evt.monto()).isEqualTo(123.45);
        assertThat(evt.medioPago()).isEqualTo("Tarjeta");
    }

    /**
     * solicitarPago(): si NO existe la orden -> no publica evento ni guarda.
     */
    @Test
    void solicitarPago_noHaceNada_siOrdenNoExiste() {
        when(ordenRepository.findById(999L)).thenReturn(Optional.empty());

        ordenService.solicitarPago(new RealizarPagoCommand(999L, "X"));

        verify(publisher, never()).publishOrdenPagoRequerido(any());
        verify(ordenRepository, never()).save(any());
    }

    /**
     * pagoConfirmado(): confirma pago, guarda y publica OrdenPagadaEvent (envío requerido).
     */
    @Test
    void pagoConfirmado_debeGuardarYPublicarEnvioRequerido() {
        Orden orden = mock(Orden.class);
        when(ordenRepository.findById(77L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(orden)).thenReturn(orden);

        when(orden.getId()).thenReturn(77L);
        when(orden.getDireccionEnviar()).thenReturn("Dir Z");

        ordenService.pagoConfirmado(77L);

        verify(orden).confirmarPago();
        verify(ordenRepository).save(orden);
        verify(publisher).publishOrdenEnvioRequerido(evtEnvioReqCaptor.capture());

        OrdenPagadaEvent evt = evtEnvioReqCaptor.getValue();
        assertThat(evt.ordenId()).isEqualTo(77L);
        assertThat(evt.direccionEnvio()).isEqualTo("Dir Z");
    }

    /**
     * pagoRechazado(): registra pago fallido y guarda.
     */
    @Test
    void pagoRechazado_debeGuardar() {
        Orden orden = mock(Orden.class);
        when(ordenRepository.findById(88L)).thenReturn(Optional.of(orden));

        ordenService.pagoRechazado(88L);

        verify(orden).registrarPagoFallido();
        verify(ordenRepository).save(orden);
    }

    /**
     * envioConfirmado(): confirma envío y guarda.
     */
    @Test
    void envioConfirmado_debeGuardar() {
        Orden orden = mock(Orden.class);
        when(ordenRepository.findById(101L)).thenReturn(Optional.of(orden));

        ordenService.envioConfirmado(101L);

        verify(orden).confirmarEnvio();
        verify(ordenRepository).save(orden);
    }

    /**
     * envioError(): marca error de envío y guarda.
     */
    @Test
    void envioError_debeGuardar() {
        Orden orden = mock(Orden.class);
        when(ordenRepository.findById(202L)).thenReturn(Optional.of(orden));

        ordenService.envioError(202L);

        verify(orden).errorEnvio();
        verify(ordenRepository).save(orden);
    }

    /**
     * getAll(): devuelve lo que retorna el repositorio.
     */
    @Test
    void getAll_debeRetornarListadoDelRepositorio() {
        var ord1 = mock(Orden.class);
        var ord2 = mock(Orden.class);
        when(ordenRepository.findAll()).thenReturn(List.of(ord1, ord2));

        List<Orden> out = ordenService.getAll();

        assertThat(out).containsExactly(ord1, ord2);
        verify(ordenRepository).findAll();
        verifyNoMoreInteractions(ordenRepository);
    }
}
