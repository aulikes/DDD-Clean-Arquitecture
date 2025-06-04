package com.aug.ecommerce.infrastructure.init;

import com.aug.ecommerce.adapters.rest.dto.RealizarOrdenRequestDTO;
import com.aug.ecommerce.adapters.rest.mapper.OrdenMapper;
import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.service.ClienteService;
import com.aug.ecommerce.application.service.OrdenService;
import com.aug.ecommerce.application.service.ProductoService;
import com.aug.ecommerce.domain.model.cliente.Cliente;
import com.aug.ecommerce.domain.model.producto.Producto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Order(4)
@Component
@RequiredArgsConstructor
public class OrdenInitializer implements ApplicationRunner {

    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final OrdenService ordenService;
    private final OrdenMapper mapper;

    @Override
    public void run(ApplicationArguments args) {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.schedule(() -> {
            try {
                List<Cliente> clientes = new ArrayList<>(clienteService.getAll());
                List<Producto> productos = new ArrayList<>(productoService.getAll());

                if (clientes.isEmpty() || productos.isEmpty()) {
                    log.warn("No se puede inicializar órdenes: no hay clientes o productos cargados.");
                    return;
                }

                Random random = new Random();
                //For para 20 órdenes
                for (int i = 1; i <= 20; i++) {
                    Cliente cliente = clientes.get(random.nextInt(clientes.size()));

                    RealizarOrdenRequestDTO request = new RealizarOrdenRequestDTO();
                    request.setClienteId(cliente.getId());
                    request.setDireccionEnviar("Dirección ficticia #" + i);
                    List<RealizarOrdenRequestDTO.ItemOrdenDTO> items;

                    if (i == 1) {
                        // Orden con producto inexistente (para probar validación fallida)
                        RealizarOrdenRequestDTO.ItemOrdenDTO itemNoValido = new RealizarOrdenRequestDTO.ItemOrdenDTO();
                        itemNoValido.setProductoId(99999L); // ID que no existe
                        itemNoValido.setCantidad(2);
                        itemNoValido.setPrecioUnitario(100.0);
                        items = List.of(itemNoValido);
                    } else {
                        // Órdenes válidas, selecciona entre 1 y 4 productos existentes aleatorios
                        Collections.shuffle(productos);
                        List<Producto> seleccionados = productos.subList(0, random.nextInt(4) + 1);

                        items = seleccionados.stream()
                                .map(producto -> {
                                    RealizarOrdenRequestDTO.ItemOrdenDTO item = new RealizarOrdenRequestDTO.ItemOrdenDTO();
                                    item.setProductoId(producto.getId());
                                    item.setCantidad(random.nextInt(5) + 1);
                                    item.setPrecioUnitario(random.nextDouble() * 100);
                                    return item;
                                })
                                .collect(Collectors.toList());
                    }
                    request.setItems(items);
                    ordenService.crearOrden(mapper.toCommand(request));
                    log.info("Orden #{} creada para cliente {}", i, cliente.getNombre());
                }
            } catch (Exception e) {
                log.error("Error al inicializar órdenes:", e);
                throw new RuntimeException(e);
            } finally {
                log.info("Creadas todas las Orden");
                // Solo se ejecuta una vez, se cierra el executor
                executor.shutdown();
            }
        }, 5, TimeUnit.SECONDS);

    }
}
