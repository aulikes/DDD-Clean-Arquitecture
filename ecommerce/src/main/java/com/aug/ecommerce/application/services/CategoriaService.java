package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.commands.CrearCategoriaCommand;
import com.aug.ecommerce.domain.models.categoria.Categoria;
import com.aug.ecommerce.domain.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
