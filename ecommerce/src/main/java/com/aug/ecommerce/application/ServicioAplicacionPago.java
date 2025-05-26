package com.aug.ecommerce.application;

import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.model.pago.Pago;
import com.aug.ecommerce.domain.event.OrdenPagada;
import com.aug.ecommerce.domain.service.ProcesadorDePago;

public class ServicioAplicacionPago {

    private final ProcesadorDePago procesadorDePago;

    public ServicioAplicacionPago(ProcesadorDePago procesadorDePago) {
        this.procesadorDePago = procesadorDePago;
    }

    public OrdenPagada confirmarPago(Pago pago, Orden orden) {
        procesadorDePago.procesar(pago, orden);
        return new OrdenPagada(orden.getId());
    }
}

