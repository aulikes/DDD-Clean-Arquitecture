package com.aug.ecommerce.infrastructure.init;

import com.aug.ecommerce.adapters.rest.dtos.CrearProductoRequestDTO;
import com.aug.ecommerce.adapters.rest.mapper.ProductoMapper;
import com.aug.ecommerce.application.services.ProductoService;
import com.aug.ecommerce.domain.model.categoria.Categoria;
import com.aug.ecommerce.domain.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductoInitializer {

    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    public void run() {
        List<Long> categorias = categoriaRepository.findAll().stream()
                .map(Categoria::getId)
                .toList();

        if (categorias.size() < 3) {
            log.info(">>> No hay suficientes categorías para crear productos.");
            return;
        }

        List<CrearProductoRequestDTO> productos = List.of(
                crear("Laptop Lenovo", "Laptop Ryzen 5", 2500000, "/img/laptop1.jpg", categorias.get(0)),
                crear("Laptop Asus", "Asus i5 11th Gen", 3100000, "/img/laptop2.jpg", categorias.get(0)),
                crear("Smartphone Samsung", "Galaxy A52", 1600000, "/img/phone1.jpg", categorias.get(1)),
                crear("Smartphone Motorola", "Moto G60", 1200000, "/img/phone2.jpg", categorias.get(1)),
                crear("Monitor LG", "Monitor Full HD 24\"", 750000, "/img/monitor1.jpg", categorias.get(2)),
                crear("Monitor HP", "Monitor 27\" IPS", 1100000, "/img/monitor2.jpg", categorias.get(2)),
                crear("Teclado Logitech", "Teclado mecánico", 300000, "/img/acc1.jpg", categorias.get(0)),
                crear("Mouse Logitech", "Mouse inalámbrico", 200000, "/img/acc2.jpg", categorias.get(0)),
                crear("Cargador Xiaomi", "Cargador rápido 33W", 90000, "/img/acc3.jpg", categorias.get(1)),
                crear("Audífonos JBL", "Bluetooth con cancelación", 400000, "/img/audio1.jpg", categorias.get(1)),
                crear("Smartwatch Huawei", "Watch GT 3", 900000, "/img/watch.jpg", categorias.get(1)),
                crear("Tablet Samsung", "Tab A8", 1300000, "/img/tablet.jpg", categorias.get(1)),
                crear("Router TP-Link", "Wi-Fi 6 AX1800", 500000, "/img/router.jpg", categorias.get(2)),
                crear("Disco Duro", "HDD 1TB Seagate", 200000, "/img/hdd.jpg", categorias.get(0)),
                crear("SSD Kingston", "SSD 512GB NVMe", 300000, "/img/ssd.jpg", categorias.get(0)),
                crear("Silla gamer", "Reclinable con soporte lumbar", 950000, "/img/silla.jpg", categorias.get(2)),
                crear("Escritorio", "Mesa para PC gamer", 700000, "/img/desk.jpg", categorias.get(2)),
                crear("Webcam Logitech", "Full HD 1080p", 320000, "/img/webcam.jpg", categorias.get(2)),
                crear("Micrófono Blue Yeti", "USB para streaming", 600000, "/img/mic.jpg", categorias.get(2)),
                crear("Base Laptop", "Con ventilador", 80000, "/img/base.jpg", categorias.get(0))
        );

        productos.forEach(dto ->
                productoService.crearProducto(productoMapper.toCommand(dto))
        );

        log.info(">>> Productos iniciales creados");
    }

    private CrearProductoRequestDTO crear(String nombre, String descripcion, double precio, String imagen, Long categoriaId) {
        CrearProductoRequestDTO dto = new CrearProductoRequestDTO();
        dto.setNombre(nombre);
        dto.setDescripcion(descripcion);
        dto.setPrecio(precio);
        dto.setCantidad(500L);
        dto.setImagenUrl(imagen);
        dto.setCategoriaId(categoriaId);
        return dto;
    }
}
