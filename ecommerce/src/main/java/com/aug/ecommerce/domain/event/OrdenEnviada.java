package com.aug.ecommerce.domain.event;

public final class OrdenEnviada {
    private final Long ordenId;

    public OrdenEnviada(Long ordenId) {
        this.ordenId = ordenId;
    }

    public Long getOrdenId() { return ordenId; }
}
