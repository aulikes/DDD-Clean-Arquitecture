package com.aug.ecommerce.domain.service;

import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.model.pago.Pago;

public class ProcesadorDePago {

    public void procesar(Pago pago, Orden orden) {
        if (!pago.getOrdenId().equals(orden.getId())) {
            throw new IllegalArgumentException("El pago no pertenece a la orden");
        }

        if (pago.getEstado() != Pago.Estado.CONFIRMADO) {
            throw new IllegalStateException("El pago no está confirmado");
        }

        if (Double.compare(pago.getMonto(), orden.calcularTotal()) != 0) {
            throw new IllegalStateException("El monto del pago no coincide");
        }

        orden.pagar();
    }
}
