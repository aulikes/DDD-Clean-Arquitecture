package com.aug.ecommerce.api.dominio.services;

import com.aug.ecommerce.api.dominio.entidades.orden.Orden;
import com.aug.ecommerce.api.dominio.entidades.envio.Envio;
import com.aug.ecommerce.api.dominio.eventos.OrdenEnviada;

public class ProcesadorDeEnvio {

    public OrdenEnviada procesar(Orden orden, Envio envio, String trackingNumber) {
        envio.despachar(trackingNumber);
        orden.enviar();
        return new OrdenEnviada(orden.getId());
    }
}
