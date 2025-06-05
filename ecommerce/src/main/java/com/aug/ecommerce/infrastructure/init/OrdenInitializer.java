package com.aug.ecommerce.infrastructure.init;

import com.aug.ecommerce.adapters.rest.dto.RealizarOrdenRequestDTO;
import com.aug.ecommerce.adapters.rest.mapper.OrdenMapper;
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
    private final StartupDelayManager startupDelayManager;

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {

        while (!startupDelayManager.isReady()) {
            try {
                Thread.sleep(500); // Espera pasiva hasta que esté listo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        int intentos = 0;
        int cantClientes = 3;
        int cantProductos = 20;

        while (intentos < 5) {
            try {
                List<Cliente> clientes = new ArrayList<>(clienteService.getAll());
                List<Producto> productos = new ArrayList<>(productoService.getAll());

                if (clientes.size() < cantClientes || productos.size() < cantProductos) {
                    log.error("Reintento #{}, Cantidad de Clientes: {}, Cantidad de Productos: {}",
                            intentos + 1, clientes.size(), productos.size());
                    Thread.sleep(5000);
                    intentos++;
                    continue;
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
                log.info("Creadas todas las Órdenes");
                return;
            } catch (Exception e) {
                log.error("Error al inicializar órdenes:", e);
                throw new RuntimeException(e);
            }
        }
        log.error("No se pudo completar la carga de órdenes tras {} intentos", intentos);
    }
}
