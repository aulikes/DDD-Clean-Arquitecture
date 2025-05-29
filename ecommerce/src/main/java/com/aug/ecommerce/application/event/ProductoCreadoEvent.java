package com.aug.ecommerce.application.event;

public record ProductoCreadoEvent(
        Long productoId,
        Long cantidad
) {
}
