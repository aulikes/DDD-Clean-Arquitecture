package com.aug.ecommerce.api.dominio.servicios;

import com.aug.ecommerce.api.dominio.entidades.orden.Orden;
import com.aug.ecommerce.api.dominio.eventos.OrdenCancelada;

public class ProcesadorDeCancelacion {

    public OrdenCancelada procesar(Orden orden) {
        orden.cancelar();
        return new OrdenCancelada(orden.getId());
    }
}
