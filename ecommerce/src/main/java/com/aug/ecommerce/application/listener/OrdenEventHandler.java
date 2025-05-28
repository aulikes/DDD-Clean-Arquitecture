package com.aug.ecommerce.application.listener;

import com.aug.ecommerce.application.event.PagoConfirmadoEvent;
import com.aug.ecommerce.application.service.OrdenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrdenEventHandler {

    private final OrdenService ordenService;

//    @Transactional
//    @EventListener
//    public void orderPagoConfirmadoEvent(PagoConfirmadoEvent evento) {
//        log.info("Evento recibido: PagoConfirmadoEvent para orden {}", evento.ordenId());
//        ordenService.promesaPagoOrden(evento.ordenId());
//    }
}
