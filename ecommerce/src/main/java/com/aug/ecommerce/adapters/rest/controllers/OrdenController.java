package com.aug.ecommerce.adapters.rest.controllers;

import com.aug.ecommerce.adapters.rest.dtos.RealizarOrdenRequestDTO;
import com.aug.ecommerce.adapters.rest.mapper.OrdenMapper;
import com.aug.ecommerce.application.services.OrdenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;
    private final OrdenMapper mapper;

    @PostMapping
    public ResponseEntity<Long> crearOrden(@Valid @RequestBody RealizarOrdenRequestDTO request) {
        return new ResponseEntity<>(ordenService.crearOrden(mapper.toCommand(request)), HttpStatus.CREATED);
    }
}
