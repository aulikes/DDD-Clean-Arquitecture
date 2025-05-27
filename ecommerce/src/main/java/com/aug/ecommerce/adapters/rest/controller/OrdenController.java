package com.aug.ecommerce.adapters.rest.controller;

import com.aug.ecommerce.adapters.rest.dto.RealizarOrdenRequestDTO;
import com.aug.ecommerce.adapters.rest.mapper.OrdenMapper;
import com.aug.ecommerce.application.service.OrdenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
