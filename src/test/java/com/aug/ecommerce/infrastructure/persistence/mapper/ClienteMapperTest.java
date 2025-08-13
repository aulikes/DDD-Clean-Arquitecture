package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.models.cliente.Cliente;
import com.aug.ecommerce.infrastructure.persistence.entity.ClienteEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.DireccionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ClienteMapper.
 * Cubre mapeo de datos básicos y direcciones (colecciones).
 */
class ClienteMapperTest {

    @Test
    @DisplayName("toEntity: mapea Cliente con direcciones a ClienteEntity")
    void toEntity_ok() {
        var c = new Cliente(77L, "Ana", "ana@mail.com");
        c.agregarDireccion("CL 1", "Bogotá", "CO", "110111");
        c.agregarDireccion("CL 2", "Medellín", "CO", "050021");

        ClienteEntity e = ClienteMapper.toEntity(c);

        assertEquals(77L, e.getId());
        assertEquals("Ana", e.getNombre());
        assertEquals("ana@mail.com", e.getEmail());
        assertNotNull(e.getDirecciones());
        assertEquals(2, e.getDirecciones().size());
        assertEquals("CL 1", e.getDirecciones().get(0).getCalle());
        assertEquals("CL 2", e.getDirecciones().get(1).getCalle());
    }

    @Test
    @DisplayName("toDomain: mapea ClienteEntity con direcciones a Cliente")
    void toDomain_ok() {
        var e = new ClienteEntity();
        e.setId(88L);
        e.setNombre("Luis");
        e.setEmail("luis@mail.com");

        var d1 = new DireccionEntity();
        d1.setCalle("C1"); d1.setCiudad("Bogotá"); d1.setPais("CO"); d1.setCodigoPostal("110111");

        var d2 = new DireccionEntity();
        d2.setCalle("C2"); d2.setCiudad("Cali"); d2.setPais("CO"); d2.setCodigoPostal("760001");

        e.setDirecciones(List.of(d1, d2));

        Cliente c = ClienteMapper.toDomain(e);

        assertEquals(88L, c.getId());
        assertEquals("Luis", c.getNombre());
        assertEquals("luis@mail.com", c.getEmail());
        assertEquals(2, c.getDirecciones().size());
        assertEquals("C1", c.getDirecciones().get(0).getCalle());
        assertEquals("C2", c.getDirecciones().get(1).getCalle());
    }
}
