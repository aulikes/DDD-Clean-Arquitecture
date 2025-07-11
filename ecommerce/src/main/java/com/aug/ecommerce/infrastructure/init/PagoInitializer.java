package com.aug.ecommerce.infrastructure.init;

import com.aug.ecommerce.application.commands.RealizarPagoCommand;
import com.aug.ecommerce.application.service.OrdenService;
import com.aug.ecommerce.domain.model.orden.EstadoOrden;
import com.aug.ecommerce.domain.model.orden.Orden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PagoInitializer {
    private final OrdenService ordenService;


    private static final AtomicInteger INTENTOS = new AtomicInteger(0);
    private static final int CANT_ORDENES = 20;

    public void run() {
        Callable<Boolean> readyPago = ejecutarPagos();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(() -> {
            try {
                Boolean resultado = readyPago.call();
                if (resultado) {
                    log.info("Creadas todos los Pagod");
                    executor.shutdownNow(); // detiene futuras ejecuciones
                }
            } catch (Exception e) {
                log.error("Exception ejecutando la tarea readyOrden: ", e);
                executor.shutdown();
            }
        }, 0, 15, TimeUnit.SECONDS);
        // Cancelar automáticamente si se excede el timeout
        executor.schedule(() -> {
            if (!executor.isShutdown()) {
                log.error("Tiempo máximo alcanzado. Cancelando tarea.");
                executor.shutdownNow();
            }
        }, 5, TimeUnit.MINUTES);
    }


    private Callable<Boolean> ejecutarPagos(){
        return () -> {
            List<Orden> ordenes = ordenService.getAll();

            if (ordenes.size() < CANT_ORDENES) {
                log.error("Reintento #{}, Cantidad de Órdenes: {}", INTENTOS.addAndGet(1), ordenes.size());
                return false;
            }
            List<Orden> candidatas = ordenes.stream()
                    .filter(o ->
                            o.getEstado().equals(EstadoOrden.deTipo(EstadoOrden.Tipo.LISTA_PARA_PAGO)))
                    .collect(Collectors.toList());

            if (candidatas.isEmpty()) {
                log.error("No se puede inicializar pagos: no hay ordenes cargadas candidatas.");
                INTENTOS.addAndGet(1);
                return false;
            }
            Random random = new Random();
            int cont = 0;
            int cant = (candidatas.size() / 2) + 1;
            while (!candidatas.isEmpty()) {
                log.info("Cantidad de Ordenes a pagar {}, iteracion #{}", cant, cont);
                int indice = random.nextInt(candidatas.size());
                Orden orden = candidatas.remove(indice); // elimina de candidatas
                ordenService.solicitarPago(new RealizarPagoCommand(orden.getId(), "Inicial"));
                cont++;
                if (cont >= cant) break;
            }
            return true;
        };
    }
}
