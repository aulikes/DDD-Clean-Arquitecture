package com.aug.ecommerce.infrastructure.init;

import com.aug.ecommerce.adapters.rest.dtos.CrearCategoriaRequestDTO;
import com.aug.ecommerce.adapters.rest.mapper.CategoriaMapper;
import com.aug.ecommerce.application.services.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoriaInitializer {

    private final CategoriaService categoriaService;
    private final CategoriaMapper mapper;

    @Transactional
    public void run() {
        List<CrearCategoriaRequestDTO> categorias = List.of(
                new CrearCategoriaRequestDTO("Laptops", "Computadores portátiles"),
                new CrearCategoriaRequestDTO("Celulares", "Teléfonos inteligentes"),
                new CrearCategoriaRequestDTO("Monitores", "Pantallas LED y LCD")
        );
        categorias.forEach(dto ->
                categoriaService.crearCategoria(mapper.toCommand(dto))
        );
        log.info(">>> Categorías iniciales creadas");
    }
}
