package com.aug.ecommerce.application.listener;

import com.aug.ecommerce.application.service.OrdenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrdenEventListener {

    private final OrdenService ordenService;

//    @Transactional
//    @EventListener
//    public void orderPagoConfirmadoEvent(PagoConfirmadoEvent evento) {
//        log.info("Evento recibido: PagoConfirmadoEvent para orden {}", evento.ordenId());
//        ordenService.promesaPagoOrden(evento.ordenId());
//    }
}
