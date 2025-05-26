package com.aug.ecommerce.api.dominio.eventos;

import java.util.UUID;

public final class OrdenEntregada {
    private final UUID ordenId;

    public OrdenEntregada(UUID ordenId) {
        this.ordenId = ordenId;
    }

    public UUID getOrdenId() { return ordenId; }
}
