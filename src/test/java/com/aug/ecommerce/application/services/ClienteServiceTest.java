package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.commands.CrearClienteCommand;
import com.aug.ecommerce.domain.models.cliente.Cliente;
import com.aug.ecommerce.domain.models.cliente.Direccion;
import com.aug.ecommerce.domain.repositories.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Estas pruebas unitarias validan el comportamiento real de ClienteService
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    ClienteRepository clienteRepository;

    @InjectMocks
    ClienteService clienteService;

    @Captor
    ArgumentCaptor<Cliente> clienteCaptor;

    /**
     * Debe construir un Cliente con las direcciones del comando y persistirlo una sola vez.
     * Se verifican los campos básicos y el tamaño de la lista de direcciones.
     */
    @Test
    void crearCliente_debeConstruirAgregarDireccionesYGuardar() {
        // Dado: comando con datos y dos direcciones
        var d1 = new CrearClienteCommand.Direccion(null, "Calle 1", "Bogotá", "CO", "110111");
        var d2 = new CrearClienteCommand.Direccion(null, "Calle 2", "Medellín", "CO", "050001");
        var cmd = new CrearClienteCommand(null, List.of(d1, d2));
        cmd.setNombre("Ana");
        cmd.setEmail("ana@mail");

                // El repositorio devuelve lo que recibe (simulando guardado)
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        // Cuando
        Cliente out = clienteService.crearCliente(cmd);

        // Entonces: se verifica la llamada de persistencia con un Cliente coherente
        verify(clienteRepository, times(1)).save(clienteCaptor.capture());
        Cliente guardado = clienteCaptor.getValue();

        assertThat(guardado.getId()).isNull();
        assertThat(guardado.getNombre()).isEqualTo("Ana");
        assertThat(guardado.getEmail()).isEqualTo("ana@mail");
        assertThat(guardado.getDirecciones()).hasSize(2);
        assertThat(guardado.getDirecciones().get(0).getCalle()).isEqualTo("Calle 1");
        assertThat(guardado.getDirecciones().get(1).getCalle()).isEqualTo("Calle 2");

        // Y: el método retorna el agregado resultante
        assertThat(out).isSameAs(guardado);
    }

    /**
     * Debe recuperar el Cliente por ID, actualizar el nombre y persistir.
     * Se valida que el nombre quede actualizado y que se invoque save una sola vez.
     */
    @Test
    void actualizarNombre_debeRecuperarMutarYGuardar() {
        // Dado
        var existente = new Cliente(10L, "Viejo", "v@mail");
        when(clienteRepository.findById(10L)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new CrearClienteCommand(10L, List.of());
        cmd.setNombre("Nuevo");
        cmd.setEmail("v@mail");

        // Cuando
        Cliente actualizado = clienteService.actualizarNombre(cmd);

        // Entonces
        verify(clienteRepository).findById(10L);
        verify(clienteRepository).save(existente);
        verifyNoMoreInteractions(clienteRepository);

        assertThat(actualizado.getNombre()).isEqualTo("Nuevo");
    }

    /**
     * Debe recuperar el Cliente por ID, actualizar el email y persistir.
     */
    @Test
    void actualizarEmail_debeRecuperarMutarYGuardar() {
        // Dado
        var existente = new Cliente(20L, "Nombre", "viejo@mail");
        when(clienteRepository.findById(20L)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new CrearClienteCommand(20L, List.of());
        cmd.setNombre("Nombre");
        cmd.setEmail("nuevo@mail");

        // Cuando
        Cliente actualizado = clienteService.actualizarEmail(cmd);

        // Entonces
        verify(clienteRepository).findById(20L);
        verify(clienteRepository).save(existente);
        verifyNoMoreInteractions(clienteRepository);

        assertThat(actualizado.getEmail()).isEqualTo("nuevo@mail");
    }

    /**
     * Debe recuperar el Cliente por ID, agregar la dirección y retornar la Dirección creada por el agregado.
     * Se valida que la dirección retornada tenga los valores esperados y que quede asociada al Cliente.
     */
    @Test
    void agregarDireccion_debeAgregarYRetornarDireccion() {
        // Dado: agregado real
        var cliente = new Cliente(30L, "Ana", "a@mail");
        when(clienteRepository.findById(30L)).thenReturn(Optional.of(cliente));

        // Cuando
        Direccion dir = clienteService.agregarDireccion(30L, "Calle 3", "Cali", "CO", "760001");

        // Entonces
        verify(clienteRepository).findById(30L);
        assertThat(dir.getCalle()).isEqualTo("Calle 3");
        assertThat(dir.getCiudad()).isEqualTo("Cali");
        assertThat(dir.getPais()).isEqualTo("CO");
        assertThat(dir.getCodigoPostal()).isEqualTo("760001");
        assertThat(cliente.getDirecciones()).contains(dir);
    }

    /**
     * Debe recuperar el Cliente y delegar en actualizarDireccion del agregado con los valores proporcionados.
     * Se usa un mock de Cliente para evitar acoplarse a la resolución interna por UUID.
     */
    @Test
    void actualizarDireccion_debeDelegarEnAgregado() {
        // Dado
        Cliente agregado = mock(Cliente.class);
        when(clienteRepository.findById(40L)).thenReturn(Optional.of(agregado));

        UUID dirId = UUID.randomUUID();

        // Cuando
        clienteService.actualizarDireccion(40L, dirId, "Nueva", "Ciudad", "PAIS", "ZIP");

        // Entonces
        verify(clienteRepository).findById(40L);
        verify(agregado).actualizarDireccion(dirId, "Nueva", "Ciudad", "PAIS", "ZIP");
        verifyNoMoreInteractions(clienteRepository);
    }

    /**
     * Debe delegar la eliminación de la dirección directamente en el agregado recibido.
     */
    @Test
    void eliminarDireccion_debeDelegarEnAgregado() {
        // Dado
        Cliente agregado = mock(Cliente.class);
        UUID dirId = UUID.randomUUID();

        // Cuando
        clienteService.eliminarDireccion(agregado, dirId);

        // Entonces
        verify(agregado).eliminarDireccion(dirId);
        verifyNoInteractions(clienteRepository);
    }

    /**
     * Debe retornar exactamente lo que expone el repositorio sin transformaciones adicionales.
     */
    @Test
    void getAll_debeRetornarListadoDelRepositorio() {
        // Dado
        when(clienteRepository.findAll()).thenReturn(List.of(
                new Cliente(1L, "A", "a@mail"),
                new Cliente(2L, "B", "b@mail")
        ));

        // Cuando
        List<Cliente> all = clienteService.getAll();

        // Entonces
        assertThat(all).hasSize(2);
        assertThat(all.get(0).getNombre()).isEqualTo("A");
        verify(clienteRepository).findAll();
        verifyNoMoreInteractions(clienteRepository);
    }
}
