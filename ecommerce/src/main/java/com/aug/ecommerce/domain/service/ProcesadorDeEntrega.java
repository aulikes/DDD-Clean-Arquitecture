package com.aug.ecommerce.domain.service;

import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.model.envio.Envio;
import com.aug.ecommerce.domain.event.OrdenEntregada;

public class ProcesadorDeEntrega {

    public OrdenEntregada procesar(Orden orden, Envio envio) {
        envio.entregar();
        orden.entregar();
        return new OrdenEntregada(orden.getId());
    }
}
