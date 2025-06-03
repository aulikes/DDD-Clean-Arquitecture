package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.dto.ResultadoPagoDTO;
import com.aug.ecommerce.application.event.OrdenAPagarEvent;
import com.aug.ecommerce.application.event.PagoConfirmadoEvent;
import com.aug.ecommerce.application.publisher.PagoEventPublisher;
import com.aug.ecommerce.application.gateway.PasarelaPagoClient;
import com.aug.ecommerce.domain.model.pago.Pago;
import com.aug.ecommerce.domain.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PasarelaPagoClient pasarelaPagoClient;
    private final PagoEventPublisher pagoEventPublisher;

    @Transactional
    public void realizarPago(OrdenAPagarEvent event) {

        Pago pago = pagoRepository.save(
                Pago.create(event.ordenId(), event.monto(), event.medioPago()));

        ResultadoPagoDTO resultado = null;
        try {
            resultado = pasarelaPagoClient.realizarPago(pago);
        } catch (TimeoutException e) {
            resultado = new ResultadoPagoDTO(false, null, e.getMessage());
        }

        if (resultado.exitoso()) {
            pago.confirmar(resultado.codigoTransaccion());
        } else {
            pago.fallar(resultado.mensaje());
        }
        pagoRepository.save(pago);
        //lanza evento
        pagoEventPublisher.publicarPagoRealizado(
            new PagoConfirmadoEvent(event.ordenId(), pago.getId(), Instant.now(),
                    resultado.exitoso(), resultado.codigoTransaccion(), resultado.mensaje()));

    }
}
