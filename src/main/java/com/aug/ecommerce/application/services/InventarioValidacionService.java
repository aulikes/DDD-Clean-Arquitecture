package com.aug.ecommerce.application.services;

import com.aug.ecommerce.application.events.*;
import com.aug.ecommerce.application.publishers.InventarioEventPublisher;
import com.aug.ecommerce.domain.repositories.InventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de aplicación encargado de coordinar la validación de inventario.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioValidacionService {

    private final InventarioRepository inventarioRepository;
    private final InventarioEventPublisher publisher;

    public void validarInventarioCreacionOrden(Long ordenId, List<OrdenCreadaEvent.ItemOrdenCreada> itemsOrden) throws Exception {
        try {
            boolean hayStockSuficiente = itemsOrden.stream().allMatch(item -> {
                return inventarioRepository.findById(item.productoId())
                        .map(inv -> inv.getStockDisponible() >= item.cantidad())
                        .orElse(false);
            });

            if (hayStockSuficiente) {
                log.debug("Stock reservado para orden {}", ordenId);
                publisher.publishStockDisponible(new InventarioValidadoEvent(ordenId));
            } else {
                throw new RuntimeException("Stock insuficiente para orden " + ordenId);
            }
        } catch (Exception e) {
            log.error("Stock insuficiente para orden {}", ordenId);
            publisher.publishStockNoDisponible(new InventarioNoValidadoEvent(ordenId));
//            throw new Exception(e);
        }

    }
}
