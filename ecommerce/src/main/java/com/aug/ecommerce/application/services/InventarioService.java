package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.commands.CrearInventarioCommand;
import com.aug.ecommerce.domain.model.inventario.Inventario;
import com.aug.ecommerce.domain.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class InventarioService {
    private final InventarioRepository inventarioRepository;

    @Transactional
    public void crearInvenario(CrearInventarioCommand crearInventarioCommand){

        Inventario inventario = inventarioRepository.findById(crearInventarioCommand.productoId())
            .map(inv -> {
                inv.aumentarStock(crearInventarioCommand.cantidad());
                return inv;
            })
            .orElseGet(() -> new Inventario(
                    crearInventarioCommand.productoId(),
                    crearInventarioCommand.cantidad()
            ));

        inventarioRepository.save(inventario);
    }

    public List<Inventario> getAll(){
        return inventarioRepository.findAll();
    }
}
