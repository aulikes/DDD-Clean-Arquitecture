package com.aug.ecommerce.adapters.rest.controllers;

import com.aug.ecommerce.adapters.rest.dtos.RealizarPagoRequestDTO;
import com.aug.ecommerce.application.commands.RealizarPagoCommand;
import com.aug.ecommerce.application.services.OrdenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test del slice web de PagoController.
 * Lo usamos como soporte para probar el wiring web real y no usar un MockMvc StandAlone
 */
@WebMvcTest(controllers = PagoController.class)
class PagoControllerWebMvcTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @TestConfiguration
    static class Stubs {
        @Bean
        OrdenService ordenService() {
            // Implementación mínima: valida que se llame sin lanzar excepción
            return new OrdenService(null, null) {
                @Override
                public void solicitarPago(RealizarPagoCommand command) {
                    if (command.idOrden() == null || command.medioPago() == null) {
                        throw new IllegalArgumentException("Command inválido (stub)");
                    }
                }
            };
        }
    }

    @Test
    void realizarPago_happyPath_200() throws Exception {
        RealizarPagoRequestDTO dto = new RealizarPagoRequestDTO();
        dto.setOrdenId(77L);
        dto.setMedioPago("TARJETA");

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void realizarPago_invalid_400() throws Exception {
        RealizarPagoRequestDTO dto = new RealizarPagoRequestDTO(); // faltan campos

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
