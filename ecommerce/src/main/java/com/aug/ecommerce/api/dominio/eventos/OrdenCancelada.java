package com.aug.ecommerce.api.dominio.eventos;

import java.util.UUID;

public final class OrdenCancelada {
    private final UUID ordenId;

    public OrdenCancelada(UUID ordenId) {
        this.ordenId = ordenId;
    }

    public UUID getOrdenId() { return ordenId; }
}
