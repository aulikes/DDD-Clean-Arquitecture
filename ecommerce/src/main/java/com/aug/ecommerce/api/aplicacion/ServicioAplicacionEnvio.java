package com.aug.ecommerce.api.aplicacion;

import com.aug.ecommerce.api.dominio.entidades.orden.Orden;
import com.aug.ecommerce.api.dominio.entidades.envio.Envio;
import com.aug.ecommerce.api.dominio.eventos.OrdenEnviada;
import com.aug.ecommerce.api.dominio.eventos.OrdenEntregada;
import com.aug.ecommerce.api.dominio.services.ProcesadorDeEnvio;
import com.aug.ecommerce.api.dominio.services.ProcesadorDeEntrega;

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
