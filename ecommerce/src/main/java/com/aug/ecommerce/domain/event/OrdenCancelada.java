package com.aug.ecommerce.domain.event;

import java.util.UUID;

public final class OrdenCancelada {
    private final UUID ordenId;

    public OrdenCancelada(UUID ordenId) {
        this.ordenId = ordenId;
    }

    public UUID getOrdenId() { return ordenId; }
}
