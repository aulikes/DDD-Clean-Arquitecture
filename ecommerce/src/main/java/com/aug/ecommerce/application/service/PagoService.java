package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.command.RealizarPagoCommand;
import com.aug.ecommerce.application.dto.ResultadoPagoDTO;
import com.aug.ecommerce.application.event.PagoConfirmadoEvent;
import com.aug.ecommerce.application.gateway.PagoEventDispatcher;
import com.aug.ecommerce.application.gateway.PasarelaPagoClient;
import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.model.pago.Pago;
import com.aug.ecommerce.domain.repository.OrdenRepository;
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
    private final OrdenRepository ordenRepository;
    private final PagoEventDispatcher eventDispatcher;

    @Transactional
    public void realizarPago(RealizarPagoCommand command) {
//        Orden orden = ordenRepository.findById(command.ordenId())
//                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
//        double monto = orden.calcularTotal(); // calculado desde el dominio

        Pago pago = pagoRepository.save(
                Pago.create(command.ordenId(), command.monto(), command.medioPago()));

        ResultadoPagoDTO resultado = pasarelaPagoClient.realizarPago(pago);

        if (resultado.exitoso()) {
            pago.confirmar(resultado.codigoTransaccion());
            eventDispatcher.publicarPagoRealizado(
                    new PagoConfirmadoEvent(command.ordenId(), command.direccionEntrega(), Instant.now())); // evento lanzado
        } else {
            pago.fallar(resultado.mensaje());
        }
        pagoRepository.save(pago);
    }
}
