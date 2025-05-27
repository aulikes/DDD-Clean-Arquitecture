package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.command.CrearProductoCommand;
import com.aug.ecommerce.domain.model.categoria.Categoria;
import com.aug.ecommerce.domain.model.producto.Producto;
import com.aug.ecommerce.domain.repository.CategoriaRepository;
import com.aug.ecommerce.domain.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public void crearProducto(CrearProductoCommand command) {

        Categoria categoria = categoriaRepository.findById(command.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada: " + command.categoriaId()));

        if (!categoria.isActiva()) {
            throw new IllegalStateException("La categoría está inactiva");
        }

        Producto producto = new Producto(
                null,
                command.nombre(),
                command.descripcion(),
                command.precio(),
                command.imagenUrl(),
                command.categoriaId()
        );

        productoRepository.save(producto);
    }
}
