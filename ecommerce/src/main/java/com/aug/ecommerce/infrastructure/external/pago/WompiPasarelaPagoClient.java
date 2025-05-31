package com.aug.ecommerce.infrastructure.external.pago;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import com.aug.ecommerce.application.dto.ResultadoPagoDTO;
import com.aug.ecommerce.application.gateway.PasarelaPagoClient;
import com.aug.ecommerce.domain.model.pago.Pago;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class WompiPasarelaPagoClient implements PasarelaPagoClient {

    private final Map<Long, ResultadoPagoDTO> pagosSimulados;
    private final CircuitBreaker circuitBreaker;
    private final Random random;

    public WompiPasarelaPagoClient(CircuitBreakerRegistry registry) {
        pagosSimulados = new ConcurrentHashMap<>();
        random = new Random();
        this.circuitBreaker = registry.circuitBreaker("processPayment");
    }

    @Override
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "processPayment", fallbackMethod = "fallbackPago")
    public ResultadoPagoDTO realizarPago(Pago pago) throws TimeoutException {
        int probabilidad = random.nextInt(100);
        if (probabilidad >= 80) {
            throw new TimeoutException("Simulación de timeout en pasarela");
        }
        else{
            try {
                // Simula latencia entre 1 y 3 segundos
                Thread.sleep(random.nextInt(2000) + 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Error de latencia simulada", e);
            }

            if (probabilidad < 50) {
                return new ResultadoPagoDTO(
                        true,
                        "TRX-WOMPI-" + UUID.randomUUID(),
                        "Pago aprobado"
                );
            } else {
                return new ResultadoPagoDTO(
                        false,
                        null,
                        "Pago rechazado por fondos insuficientes"
                );
            }
        }
    }

    public ResultadoPagoDTO fallbackPago(Pago pago, Throwable throwable) {
        return new ResultadoPagoDTO(
                false,
                null,
                "Servicio de pagos no disponible (Circuit Breaker activo)"
        );
    }

    public Optional<ResultadoPagoDTO> consultarResultado(Long pagoId) {
        return Optional.ofNullable(pagosSimulados.get(pagoId));
    }


    @PostConstruct
    public void init() { //Registra cuando cambia de estado
        circuitBreaker.getEventPublisher()
                .onStateTransition(event ->
                        log.info("####### TRANSITION [{}] -> [{}]",
                                event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState()));
    }
}


