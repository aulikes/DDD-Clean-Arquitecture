package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.commands.CrearCategoriaCommand;
import com.aug.ecommerce.domain.models.categoria.Categoria;
import com.aug.ecommerce.domain.repositories.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Estas pruebas unitarias validan el comportamiento de CategoriaService:
 *  - crearCategoria(): construye el dominio desde el comando y delega en el repositorio.
 *  - getAll(): retorna el listado del repositorio sin transformaciones adicionales.
 *
 * Se emplea inyección de mocks con @InjectMocks para evitar construcción manual.
 * Se verifica el orden de interacción con el repositorio y se validan los argumentos con argThat.
 */
@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    CategoriaRepository categoriaRepository;

    @InjectMocks
    CategoriaService categoriaService;

    /**
     * Debe construir una entidad de dominio coherente a partir del comando y
     * delegar una única llamada de persistencia al repositorio.
     */
    @Test
    void crearCategoria_debeConstruirDominioYGuardar() {
        // Dado: un comando válido
        CrearCategoriaCommand cmd = new CrearCategoriaCommand("Tecnología", "Electrónica y gadgets");

        // Dado: el repositorio devuelve la misma instancia que recibe (simulación de guardado)
        // Esto permite luego afirmar sobre el mismo objeto pasado a save(...)
        Mockito.when(categoriaRepository.save(Mockito.any(Categoria.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Cuando: se ejecuta el caso de uso
        categoriaService.crearCategoria(cmd);

        // Entonces: se verifica el orden y la llamada única al repositorio
        InOrder inOrder = inOrder(categoriaRepository);
        inOrder.verify(categoriaRepository).save(argThat(cat ->
                // Se espera que al crearse aún no tenga ID asignado por persistencia
                (cat.getId() == null)
                        && "Tecnología".equals(cat.getNombre())
                        && "Electrónica y gadgets".equals(cat.getDescripcion())
        ));
        inOrder.verifyNoMoreInteractions();

        // Y: se confirma que efectivamente fue una sola invocación
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    /**
     * Debe retornar exactamente lo que expone el repositorio sin modificaciones.
     */
    @Test
    void getAll_debeRetornarListadoDelRepositorio() {
        // Dado: el repositorio tiene registros
        when(categoriaRepository.findAll())
                .thenReturn(List.of(
                        new Categoria(1L, "Hogar", "Artículos para el hogar"),
                        new Categoria(2L, "Moda", "Ropa y accesorios")
                ));

        // Cuando
        List<Categoria> result = categoriaService.getAll();

        // Entonces: se valida el tamaño y contenido principal
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getNombre()).isEqualTo("Hogar");
        assertThat(result.get(1).getNombre()).isEqualTo("Moda");

        // Y: se verifica la interacción con el repositorio
        verify(categoriaRepository, times(1)).findAll();
        verifyNoMoreInteractions(categoriaRepository);
    }
}
