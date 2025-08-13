package com.aug.ecommerce.adapters.rest.controllers;

import com.aug.ecommerce.adapters.rest.dtos.RealizarOrdenRequestDTO;
import com.aug.ecommerce.adapters.rest.mappers.OrdenMapper;
import com.aug.ecommerce.application.commands.RealizarOrdenCommand;
import com.aug.ecommerce.application.services.OrdenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test del slice web de OrdenController.
 * Lo usamos como soporte para probar el wiring web real y no usar un MockMvc StandAlone
 */
@WebMvcTest(controllers = OrdenController.class)
class OrdenControllerWebMvcTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @TestConfiguration
    static class Stubs {
        @Bean
        OrdenService ordenService() {
            // Stub que devuelve un ID fijo
            return new OrdenService(null, null) {
                @Override
                public Long crearOrden(RealizarOrdenCommand command) { return 123L; }
            };
        }

        @Bean
        OrdenMapper ordenMapper() {
            // Stub que imita el mapeo de MapStruct
            return new OrdenMapper() {
                @Override
                public RealizarOrdenCommand toCommand(RealizarOrdenRequestDTO dto) {
                    List<RealizarOrdenCommand.Item> items = dto.getItems().stream()
                            .map(i -> new RealizarOrdenCommand.Item(i.getProductoId(), i.getCantidad(), i.getPrecioUnitario()))
                            .toList();
                    return new RealizarOrdenCommand(dto.getClienteId(), items, dto.getDireccionEnviar());
                }
                @Override
                public RealizarOrdenCommand.Item toItem(RealizarOrdenRequestDTO.ItemOrdenDTO dto) {
                    return new RealizarOrdenCommand.Item(dto.getProductoId(), dto.getCantidad(), dto.getPrecioUnitario());
                }
            };
        }
    }

    @Test
    void crearOrden_happyPath_201() throws Exception {
        RealizarOrdenRequestDTO.ItemOrdenDTO item = new RealizarOrdenRequestDTO.ItemOrdenDTO();
        item.setProductoId(10L);
        item.setCantidad(2);
        item.setPrecioUnitario(50.0);

        RealizarOrdenRequestDTO req = new RealizarOrdenRequestDTO();
        req.setClienteId(1L);
        req.setItems(List.of(item));
        req.setDireccionEnviar("Calle 123 #45-67");

        mockMvc.perform(post("/api/ordenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().string("123"));
    }

    @Test
    void crearOrden_invalid_400() throws Exception {
        RealizarOrdenRequestDTO req = new RealizarOrdenRequestDTO();
        req.setItems(List.of()); // @NotEmpty

        mockMvc.perform(post("/api/ordenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
