package com.aug.ecommerce.api.dominio.eventos;

import java.util.UUID;

public final class OrdenPagada {
    private final UUID ordenId;

    public OrdenPagada(UUID ordenId) {
        this.ordenId = ordenId;
    }

    public UUID getOrdenId() { return ordenId; }
}
