package com.aug.ecommerce.adapters.rest.controllers;

import com.aug.ecommerce.adapters.rest.dtos.RealizarPagoRequestDTO;
import com.aug.ecommerce.application.commands.RealizarPagoCommand;
import com.aug.ecommerce.application.services.OrdenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test unitario puro del PagoController:
 * - Verifica envío del RealizarPagoCommand (record con accessors idOrden(), medioPago()).
 * - Retorna 200 vacío en el happy path.
 */
@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    @Mock private OrdenService ordenService;

    @InjectMocks
    private PagoController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void realizarPago_ok_200_yCommandCorrecto() throws Exception {
        RealizarPagoRequestDTO dto = new RealizarPagoRequestDTO();
        dto.setOrdenId(77L);
        dto.setMedioPago("TARJETA");

        ArgumentCaptor<RealizarPagoCommand> captor = ArgumentCaptor.forClass(RealizarPagoCommand.class);

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(ordenService, times(1)).solicitarPago(captor.capture());
        RealizarPagoCommand cmd = captor.getValue();
        // OJO: es un record => accessors idOrden() y medioPago()
        org.junit.jupiter.api.Assertions.assertEquals(77L, cmd.idOrden());
        org.junit.jupiter.api.Assertions.assertEquals("TARJETA", cmd.medioPago());
        verifyNoMoreInteractions(ordenService);
    }

    @Test
    void realizarPago_invalido_400() throws Exception {
        RealizarPagoRequestDTO dto = new RealizarPagoRequestDTO(); // faltan campos @NotNull

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(ordenService);
    }
}
