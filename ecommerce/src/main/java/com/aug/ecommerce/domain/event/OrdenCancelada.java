package com.aug.ecommerce.domain.event;


public final class OrdenCancelada {
    private final Long ordenId;

    public OrdenCancelada(Long ordenId) {
        this.ordenId = ordenId;
    }

    public Long getOrdenId() { return ordenId; }
}
