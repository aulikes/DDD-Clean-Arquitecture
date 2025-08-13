package com.aug.ecommerce.adapters.rest.controllers;

import com.aug.ecommerce.adapters.rest.dtos.RealizarOrdenRequestDTO;
import com.aug.ecommerce.adapters.rest.mappers.OrdenMapper;
import com.aug.ecommerce.application.commands.RealizarOrdenCommand;
import com.aug.ecommerce.application.services.OrdenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test unitario puro del controller:
 * - Sin ApplicationContext de Spring.
 * - Dependencias mockeadas con Mockito (@Mock + @InjectMocks).
 * - MockMvc en modo standalone con validador para @Valid.
 */
@ExtendWith(MockitoExtension.class)
class OrdenControllerTest {

    @Mock private OrdenService ordenService;
    @Mock private OrdenMapper ordenMapper;

    @InjectMocks
    private OrdenController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)   // habilita Bean Validation (@Valid)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void crearOrden_RequestValida_retorna201ConId() throws Exception {
        // Arrange: request válido según tu DTO
        RealizarOrdenRequestDTO.ItemOrdenDTO item = new RealizarOrdenRequestDTO.ItemOrdenDTO();
        item.setProductoId(10L);
        item.setCantidad(2);
        item.setPrecioUnitario(50.0);

        RealizarOrdenRequestDTO req = new RealizarOrdenRequestDTO();
        req.setClienteId(1L);
        req.setItems(List.of(item));
        req.setDireccionEnviar("Calle 123 #45-67");

        // El mapper de MapStruct se mockea y devuelve el comando esperado por el service
        RealizarOrdenCommand cmd = new RealizarOrdenCommand(
                1L,
                List.of(new RealizarOrdenCommand.Item(10L, 2, 50.0)),
                "Calle 123 #45-67"
        );

        when(ordenMapper.toCommand(any(RealizarOrdenRequestDTO.class))).thenReturn(cmd);
        when(ordenService.crearOrden(cmd)).thenReturn(123L);

        // Act & Assert
        mockMvc.perform(post("/api/ordenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().string("123"));

        verify(ordenMapper, times(1)).toCommand(any(RealizarOrdenRequestDTO.class));
        verify(ordenService, times(1)).crearOrden(cmd);
        verifyNoMoreInteractions(ordenMapper, ordenService);
    }

    @Test
    void crearOrden_RequestInvalida_itemsVacios_retorna400() throws Exception {
        // Arrange: inválido por @NotEmpty en items (y podría faltar clienteId)
        RealizarOrdenRequestDTO req = new RealizarOrdenRequestDTO();
        req.setItems(List.of());

        // Act & Assert
        mockMvc.perform(post("/api/ordenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(ordenMapper, ordenService);
    }
}
