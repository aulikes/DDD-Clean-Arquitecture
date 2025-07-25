package com.aug.ecommerce.adapters.rest.controllers;

import com.aug.ecommerce.adapters.rest.dtos.RealizarPagoRequestDTO;
import com.aug.ecommerce.application.commands.RealizarPagoCommand;
import com.aug.ecommerce.application.services.OrdenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pagos")
public class PagoController {

    private final OrdenService ordenService;

    @PostMapping
    public ResponseEntity<Void> realizarPago(@Valid @RequestBody RealizarPagoRequestDTO dto) {
        ordenService.solicitarPago(new RealizarPagoCommand(dto.getOrdenId(), dto.getMedioPago()));
        return ResponseEntity.ok().build();
    }
}
