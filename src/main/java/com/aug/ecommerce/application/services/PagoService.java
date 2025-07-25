package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.dtos.ResultadoPagoDTO;
import com.aug.ecommerce.application.events.OrdenPreparadaParaPagoEvent;
import com.aug.ecommerce.application.events.PagoConfirmadoEvent;
import com.aug.ecommerce.application.publishers.PagoEventPublisher;
import com.aug.ecommerce.application.gateways.PasarelaPagoClient;
import com.aug.ecommerce.domain.models.pago.Pago;
import com.aug.ecommerce.domain.repositories.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Service
@Slf4j
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PasarelaPagoClient pasarelaPagoClient;
    private final PagoEventPublisher pagoEventPublisher;

    @Transactional
    public void realizarPago(OrdenPreparadaParaPagoEvent event) {

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
            log.debug("---> Pago confirmado, codigoTransaccion: {}", resultado.codigoTransaccion());
        } else {
            pago.fallar(resultado.mensaje());
            log.debug("---> Pago ERROR, mensaje: {}", resultado.mensaje());
        }
        pagoRepository.save(pago);
        log.debug("---> Pago realizado y guardado. Próximo a publicar");
        //lanza evento
        pagoEventPublisher.publicarPagoRealizado(
            new PagoConfirmadoEvent(event.ordenId(), pago.getId(), Instant.now(),
                    resultado.exitoso(), resultado.codigoTransaccion(), resultado.mensaje()));

    }
}
