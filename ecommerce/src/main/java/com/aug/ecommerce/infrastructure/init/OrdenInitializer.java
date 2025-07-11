package com.aug.ecommerce.infrastructure.init;

import com.aug.ecommerce.adapters.rest.dtos.RealizarOrdenRequestDTO;
import com.aug.ecommerce.adapters.rest.mappers.OrdenMapper;
import com.aug.ecommerce.application.services.ClienteService;
import com.aug.ecommerce.application.services.InventarioService;
import com.aug.ecommerce.application.services.OrdenService;
import com.aug.ecommerce.application.services.ProductoService;
import com.aug.ecommerce.domain.model.cliente.Cliente;
import com.aug.ecommerce.domain.model.inventario.Inventario;
import com.aug.ecommerce.domain.model.producto.Producto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrdenInitializer {

    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final OrdenService ordenService;
    private final InventarioService inventarioService;
    private final OrdenMapper mapper;

    private static final AtomicInteger INTENTOS = new AtomicInteger(0);
    private static final int CANT_CLIENTES = 3;
    private static final int CANT_PRODUCTOS = 20;

    public void run() {
        Callable<Boolean> readyOrden = ejecutarOrdenes();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(() -> {
            try {
                Boolean resultado = readyOrden.call();
                if (resultado) {
                    log.info("Creadas todas las Órdenes");
                    executor.shutdownNow(); // detiene futuras ejecuciones
                }
            } catch (Exception e) {
                log.error("Exception ejecutando la tarea readyOrden: ", e);
                executor.shutdown();
            }
        }, 0, 5, TimeUnit.SECONDS);
        // Cancelar automáticamente si se excede el timeout
        executor.schedule(() -> {
            if (!executor.isShutdown()) {
                log.error("Tiempo máximo alcanzado. Cancelando tarea.");
                executor.shutdownNow();
            }
        }, 2, TimeUnit.MINUTES);
    }

    private Callable<Boolean> ejecutarOrdenes(){
        return () -> {
            List<Cliente> clientes = new ArrayList<>(clienteService.getAll());
            List<Producto> productos = new ArrayList<>(productoService.getAll());
            List<Inventario> inventario = new ArrayList<>(inventarioService.getAll());

            if (clientes.size() < CANT_CLIENTES || productos.size() < CANT_PRODUCTOS || inventario.size() < CANT_PRODUCTOS) {
                log.error("Reintento #{}, Cantidad de Clientes: {}, " +
                                "Cantidad de Productos: {}, Cantidad de Inventarios: {}",
                        INTENTOS.addAndGet(1), clientes.size(), productos.size(), inventario.size());
                return false;
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
                } else if (i == 2) {
                    // Orden con inventario fuera de stock (para probar validación fallida)
                    RealizarOrdenRequestDTO.ItemOrdenDTO itemNoValido = new RealizarOrdenRequestDTO.ItemOrdenDTO();
                    itemNoValido.setProductoId(3L);
                    itemNoValido.setCantidad(90000); // inventario fuera de stock
                    itemNoValido.setPrecioUnitario(100.0);
                    items = List.of(itemNoValido);
                } else {
                    // Órdenes válidas, selecciona entre 1 y 4 productos existentes aleatorios
                    Collections.shuffle(productos);  //baraja la lista
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
            return true;
        };
    }
}
