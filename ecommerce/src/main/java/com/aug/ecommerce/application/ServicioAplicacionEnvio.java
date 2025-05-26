package com.aug.ecommerce.application;

import com.aug.ecommerce.domain.model.orden.Orden;
import com.aug.ecommerce.domain.model.envio.Envio;
import com.aug.ecommerce.domain.event.OrdenEnviada;
import com.aug.ecommerce.domain.event.OrdenEntregada;
import com.aug.ecommerce.domain.service.ProcesadorDeEnvio;
import com.aug.ecommerce.domain.service.ProcesadorDeEntrega;

public class ServicioAplicacionEnvio {

    private final ProcesadorDeEnvio procesadorDeEnvio;
    private final ProcesadorDeEntrega procesadorDeEntrega;

    public ServicioAplicacionEnvio(
            ProcesadorDeEnvio procesadorDeEnvio,
            ProcesadorDeEntrega procesadorDeEntrega
    ) {
        this.procesadorDeEnvio = procesadorDeEnvio;
        this.procesadorDeEntrega = procesadorDeEntrega;
    }

    public OrdenEnviada despachar(Orden orden, Envio envio, String tracking) {
        return procesadorDeEnvio.procesar(orden, envio, tracking);
    }

    public OrdenEntregada entregar(Orden orden, Envio envio) {
        return procesadorDeEntrega.procesar(orden, envio);
    }
}
