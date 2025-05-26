package com.aug.ecommerce.domain.event;

import java.util.UUID;

public final class OrdenEntregada {
    private final UUID ordenId;

    public OrdenEntregada(UUID ordenId) {
        this.ordenId = ordenId;
    }

    public UUID getOrdenId() { return ordenId; }
}
