package com.aug.ecommerce.domain.service;

import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.event.OrdenCancelada;

public class ProcesadorDeCancelacion {

    public OrdenCancelada procesar(Orden orden) {
        orden.cancelar();
        return new OrdenCancelada(orden.getId());
    }
}
