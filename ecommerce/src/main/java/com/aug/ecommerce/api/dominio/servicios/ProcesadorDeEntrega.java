package com.aug.ecommerce.api.dominio.services;

import com.aug.ecommerce.api.dominio.entidades.orden.Orden;
import com.aug.ecommerce.api.dominio.entidades.envio.Envio;
import com.aug.ecommerce.api.dominio.eventos.OrdenEntregada;

public class ProcesadorDeEntrega {

    public OrdenEntregada procesar(Orden orden, Envio envio) {
        envio.entregar();
        orden.entregar();
        return new OrdenEntregada(orden.getId());
    }
}
