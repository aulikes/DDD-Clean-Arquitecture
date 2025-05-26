package com.aug.ecommerce.api.aplicacion;

import com.aug.ecommerce.api.dominio.entidades.inventario.Inventario;

import java.util.UUID;

public class ServicioAplicacionInventario {

    public void reservarStock(Inventario inventario, int cantidad) {
        inventario.disminuirStock(cantidad);
    }

    public void liberarStock(Inventario inventario, int cantidad) {
        inventario.aumentarStock(cantidad);
    }

    public boolean hayStockDisponible(Inventario inventario, int cantidad) {
        return inventario.tieneStockDisponible(cantidad);
    }
}
