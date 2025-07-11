package com.aug.ecommerce.infrastructure.external.pago;

import com.aug.ecommerce.application.dtos.ResultadoPagoDTO;
import com.aug.ecommerce.application.gateways.PasarelaPagoClient;
import com.aug.ecommerce.domain.model.pago.Pago;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Component
public class WompiPasarelaPagoClient implements PasarelaPagoClient {

    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final Random random;

    public WompiPasarelaPagoClient(CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry) {
        random = new Random();
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("processPaymentWompiCB");
        this.retry = retryRegistry.retry("processPaymentWompiRetry");
    }

    @Override
    public ResultadoPagoDTO realizarPago(Pago pago) {
        // Lógica principal encapsulada
        Supplier<ResultadoPagoDTO> coreLogic = () -> {
            int probabilidad = random.nextInt(100);
            if (probabilidad >= 80) {
                throw new RuntimeException("Simulación de timeout en pasarela");
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
        };
        // Retry envuelve la lógica interna, y de esta manera, el # de intentos de RETRY, son uno solo para CB
        Supplier<ResultadoPagoDTO> retryWrapped = Retry.decorateSupplier(retry, coreLogic);
        // CircuitBreaker envuelve el Retry como una unidad
        Supplier<ResultadoPagoDTO> circuitBreakerWrapped = CircuitBreaker.decorateSupplier(circuitBreaker, retryWrapped);
        // Ejecutar con fallback en caso de excepción global
        try {
            return circuitBreakerWrapped.get();
        } catch (Exception ex) {
            return fallbackPayment(pago, ex);
        }
    }

    public ResultadoPagoDTO fallbackPayment(Pago pago, Throwable throwable) {
        return new ResultadoPagoDTO(
                false,
                null,
                "Servicio de pagos no disponible (Circuit Breaker activo)"
        );
    }

}


