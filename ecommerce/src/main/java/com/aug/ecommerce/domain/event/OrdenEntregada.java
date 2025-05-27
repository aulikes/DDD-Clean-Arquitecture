package com.aug.ecommerce.domain.event;


public final class OrdenEntregada {
    private final Long ordenId;

    public OrdenEntregada(Long ordenId) {
        this.ordenId = ordenId;
    }

    public Long getOrdenId() { return ordenId; }
}
