package com.aug.ecommerce.domain.service;

import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.model.envio.Envio;
import com.aug.ecommerce.domain.event.OrdenEnviada;

public class ProcesadorDeEnvio {

    public OrdenEnviada procesar(Orden orden, Envio envio, String trackingNumber) {
        envio.despachar(trackingNumber);
        orden.enviar();
        return new OrdenEnviada(orden.getId());
    }
}
