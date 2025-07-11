package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.commands.CrearProductoCommand;
import com.aug.ecommerce.application.events.ProductoCreadoEvent;
import com.aug.ecommerce.application.publishers.ProductoEventPublisher;
import com.aug.ecommerce.domain.model.categoria.Categoria;
import com.aug.ecommerce.domain.model.producto.Producto;
import com.aug.ecommerce.domain.repository.CategoriaRepository;
import com.aug.ecommerce.domain.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoEventPublisher productoEventPublisher;

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

        producto = productoRepository.save(producto);
        productoEventPublisher.publicarProductoCreado(new ProductoCreadoEvent(producto.getId(), command.cantidad()));
    }

    public List<Producto> getAll(){
        return productoRepository.findAll();
    }
}
