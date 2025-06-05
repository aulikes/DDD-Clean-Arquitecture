package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.command.CrearCategoriaCommand;
import com.aug.ecommerce.domain.model.categoria.Categoria;
import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public void crearCategoria(CrearCategoriaCommand command) {
        Categoria categoria = new Categoria(
                null,
                command.nombre(),
                command.descripcion()
        );
        categoriaRepository.save(categoria);
    }

    @Transactional
    public List<Categoria> getAll() {
        return categoriaRepository.findAll();
    }

}
