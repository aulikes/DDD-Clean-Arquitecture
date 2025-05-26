package com.aug.ecommerce.application;

import com.aug.ecommerce.domain.model.inventario.Inventario;

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
