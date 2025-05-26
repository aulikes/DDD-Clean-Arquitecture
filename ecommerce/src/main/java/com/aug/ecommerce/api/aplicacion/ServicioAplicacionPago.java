package com.aug.ecommerce.api.aplicacion;

import com.aug.ecommerce.api.dominio.entidades.orden.Orden;
import com.aug.ecommerce.api.dominio.entidades.pago.Pago;
import com.aug.ecommerce.api.dominio.eventos.OrdenPagada;
import com.aug.ecommerce.api.dominio.servicios.ProcesadorDePago;

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

