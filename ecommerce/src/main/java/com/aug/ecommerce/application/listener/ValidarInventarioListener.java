package com.aug.ecommerce.application.listener;

import com.aug.ecommerce.application.event.ClienteNoValidoEvent;
import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.event.StockDisponibleEvent;
import com.aug.ecommerce.application.event.StockNoDisponibleEvent;
import com.aug.ecommerce.application.publisher.InventarioEventPublisher;
import com.aug.ecommerce.domain.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidarInventarioListener {

    private final InventarioRepository inventarioRepository;
    private final InventarioEventPublisher publisher;

    @EventListener
    public void handle(OrdenCreadaEvent event) throws Exception {
        log.debug("---> Entrando al Listener ValidarInventarioListener - OrdenCreadaEvent {}", event.getOrdenId());
        try {
            boolean hayStockSuficiente = event.getItems().stream().allMatch(item -> {
                return inventarioRepository.findById(item.getProductoId())
                        .map(inv -> inv.getStockDisponible() >= item.getCantidad())
                        .orElse(false);
            });

            if (hayStockSuficiente) {
                log.debug("Stock reservado para orden {}", event.getOrdenId());
                publisher.publishStockDisponible(new StockDisponibleEvent(event.getOrdenId()));
            } else {
                throw new RuntimeException("Stock insuficiente para orden " + event.getOrdenId());
            }
        } catch (Exception e) {
            log.error("Stock insuficiente para orden {}", event.getOrdenId());
            publisher.publishStockNoDisponible(new StockNoDisponibleEvent(event.getOrdenId()));
            throw new Exception(e);
        }
    }
}
