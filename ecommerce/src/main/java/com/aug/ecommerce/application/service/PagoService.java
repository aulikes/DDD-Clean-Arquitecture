package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.dto.ResultadoPagoDTO;
import com.aug.ecommerce.application.event.OrderPaymentRequestedEvent;
import com.aug.ecommerce.application.event.PagoConfirmadoEvent;
import com.aug.ecommerce.application.publisher.PagoEventDispatcher;
import com.aug.ecommerce.application.gateway.PasarelaPagoClient;
import com.aug.ecommerce.domain.model.pago.Pago;
import com.aug.ecommerce.domain.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PasarelaPagoClient pasarelaPagoClient;
    private final PagoEventDispatcher eventDispatcher;

    @Transactional
    public void realizarPago(OrderPaymentRequestedEvent event) {

        Pago pago = pagoRepository.save(
                Pago.create(event.ordenId(), event.monto(), event.medioPago()));

        ResultadoPagoDTO resultado = pasarelaPagoClient.realizarPago(pago);

        if (resultado.exitoso()) {
            pago.confirmar(resultado.codigoTransaccion());
            eventDispatcher.publicarPagoRealizado(
                    new PagoConfirmadoEvent(event.ordenId(), event.direccionEntrega(), Instant.now())); // evento lanzado
        } else {
            pago.fallar(resultado.mensaje());
        }
        pagoRepository.save(pago);
    }
}
