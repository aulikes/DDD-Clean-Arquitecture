package com.aug.ecommerce.domain.event;

import java.util.UUID;

public final class OrdenPagada {
    private final UUID ordenId;

    public OrdenPagada(UUID ordenId) {
        this.ordenId = ordenId;
    }

    public UUID getOrdenId() { return ordenId; }
}
