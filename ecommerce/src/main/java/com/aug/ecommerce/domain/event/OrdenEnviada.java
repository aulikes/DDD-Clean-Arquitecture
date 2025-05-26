package com.aug.ecommerce.domain.event;

import java.util.UUID;

public final class OrdenEnviada {
    private final UUID ordenId;

    public OrdenEnviada(UUID ordenId) {
        this.ordenId = ordenId;
    }

    public UUID getOrdenId() { return ordenId; }
}
