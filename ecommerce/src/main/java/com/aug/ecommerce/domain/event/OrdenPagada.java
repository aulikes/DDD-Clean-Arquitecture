package com.aug.ecommerce.domain.event;

public final class OrdenPagada {
    private final Long ordenId;

    public OrdenPagada(Long ordenId) {
        this.ordenId = ordenId;
    }

    public Long getOrdenId() { return ordenId; }
}
