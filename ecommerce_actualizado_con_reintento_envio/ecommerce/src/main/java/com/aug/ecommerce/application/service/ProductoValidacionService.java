package com.aug.ecommerce.application.service;

import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.event.ProductoNoValidadoEvent;
import com.aug.ecommerce.application.event.ProductoValidadoEvent;
import com.aug.ecommerce.application.publisher.ProductoEventPublisher;
import com.aug.ecommerce.domain.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de aplicación encargado de coordinar la validación de clientes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoValidacionService {

    private final ProductoRepository productoRepository;
    private final ProductoEventPublisher publisher;

    public void validarProductoCreacionOrden(Long ordenId, List<OrdenCreadaEvent.ItemOrdenCreada> itemsOrden) {
        try {
            boolean todosExisten = itemsOrden.stream()
                    .allMatch(item -> productoRepository.findById(item.productoId()).isPresent());

            if (todosExisten) {
                log.debug("Todos los productos válidos para orden {}", ordenId);
                publisher.publishProductoValido(new ProductoValidadoEvent(ordenId));
            } else {
                throw new RuntimeException("Productos NO válidos para orden " + ordenId);
            }
        } catch (Exception ex) {
            log.error(("Productos No válidos para orden " + ordenId), ex);
            publisher.publishProductoNoValido(new ProductoNoValidadoEvent(ordenId));
        }

    }
}
