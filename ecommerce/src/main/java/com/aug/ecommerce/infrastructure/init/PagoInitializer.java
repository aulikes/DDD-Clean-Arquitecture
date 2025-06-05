package com.aug.ecommerce.infrastructure.init;

import com.aug.ecommerce.application.command.RealizarPagoCommand;
import com.aug.ecommerce.application.service.OrdenService;
import com.aug.ecommerce.domain.model.orden.EstadoOrden;
import com.aug.ecommerce.domain.model.orden.Orden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Order(5)
@Component
@RequiredArgsConstructor
public class PagoInitializer implements ApplicationRunner {
    private final OrdenService ordenService;
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
        int cantOrdenes = 20;
        int intentos = 0;

        while (intentos < 5) {
            try {
                List<Orden> ordenes = ordenService.getAll();

                if (ordenes.size() < cantOrdenes) {
                    log.error("Reintento #{}, Cantidad de Órdenes: {}", intentos + 1, ordenes.size());
                    Thread.sleep(5000);
                    intentos++;
                    continue;
                }
                List<Orden> candidatas = ordenes.stream()
                        .filter(o ->
                                o.getEstado().equals(EstadoOrden.deTipo(EstadoOrden.Tipo.LISTA_PARA_PAGO)))
                        .collect(Collectors.toList());

                if (candidatas.isEmpty()) {
                    log.error("No se puede inicializar pagos: no hay ordenes cargadas candidatas.");
                    Thread.sleep(5000);
                    intentos++;
                    continue;
                }
                Random random = new Random();
                int cont = 0;
                int cant = (candidatas.size() / 2) + 1;
                while (!candidatas.isEmpty()) {
                    int indice = random.nextInt(candidatas.size());
                    Orden orden = candidatas.remove(indice); // elimina de candidatas
                    ordenService.solicitarPago(new RealizarPagoCommand(orden.getId(), "Inicial"));
                    cont++;
                    if (cont >= cant) break;
                }
                return;
            } catch (Exception e) {
                log.error("Error al inicializar pagos:", e);
                throw new RuntimeException(e);
            }
        }
    }
}
