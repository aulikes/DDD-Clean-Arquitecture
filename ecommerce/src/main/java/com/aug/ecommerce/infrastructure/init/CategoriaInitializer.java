package com.aug.ecommerce.infrastructure.init;

import com.aug.ecommerce.adapters.rest.dto.CrearCategoriaRequestDTO;
import com.aug.ecommerce.adapters.rest.mapper.CategoriaMapper;
import com.aug.ecommerce.application.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class CategoriaInitializer implements ApplicationRunner {

    private final CategoriaService categoriaService;
    private final CategoriaMapper mapper;
    private final StartupDelayManager startupDelayManager;

    @Override
    public void run(ApplicationArguments args) {
        while (!startupDelayManager.isReady()) {
            try {
                Thread.sleep(500); // Espera pasiva hasta que esté listo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

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
