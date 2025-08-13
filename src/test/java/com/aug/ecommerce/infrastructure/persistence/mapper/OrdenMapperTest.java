package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.models.orden.EstadoOrden;
import com.aug.ecommerce.domain.models.orden.ItemOrden;
import com.aug.ecommerce.domain.models.orden.Orden;
import com.aug.ecommerce.infrastructure.persistence.entity.ItemOrdenEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.OrdenEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para OrdenMapper.
 * Cubre ida y vuelta entre dominio y entidad, incluidos ítems.
 */
class OrdenMapperTest {

    @Test
    @DisplayName("toDomain: reconstruye Orden (estado, error) y sus items")
    void toDomain_ok() {
        // Entidad con estado y error + 2 ítems
        var entity = new OrdenEntity();
        entity.setId(1001L);
        entity.setClienteId(2002L);
        entity.setDireccionEnviar("CL 1 # 2-3");
        entity.setEstado(EstadoOrden.Tipo.LISTA_PARA_PAGO.name());
        entity.setError("warning");

        var i1 = new ItemOrdenEntity();
        i1.setId(1L); i1.setProductoId(10L); i1.setCantidad(2); i1.setPrecioUnitario(5.0);

        var i2 = new ItemOrdenEntity();
        i2.setId(2L); i2.setProductoId(20L); i2.setCantidad(1); i2.setPrecioUnitario(15.0);

        var items = new ArrayList<ItemOrdenEntity>();
        items.add(i1); items.add(i2);
        entity.setItems(items);

        Orden domain = OrdenMapper.toDomain(entity);

        assertEquals(1001L, domain.getId());
        assertEquals(2002L, domain.getClienteId());
        assertEquals("CL 1 # 2-3", domain.getDireccionEnviar());
        assertEquals(EstadoOrden.deTipo(EstadoOrden.Tipo.LISTA_PARA_PAGO), domain.getEstado());
        assertEquals("warning", domain.getError());
        assertEquals(2, domain.getItems().size());
        assertEquals(10L, domain.getItems().get(0).getProductoId());
        assertEquals(2, domain.getItems().get(0).getCantidad());
        assertEquals(20L, domain.getItems().get(1).getProductoId());
        assertEquals(1, domain.getItems().get(1).getCantidad());
    }

    @Test
    @DisplayName("toEntity: mapea Orden y sus ítems a entidad")
    void toEntity_ok() {
        // Dominio usando fromPersistence para evitar reglas de transición en tests
        List<ItemOrden> items = List.of(
                ItemOrden.fromPersistence(1L, 10L, 2, 5.0),
                ItemOrden.fromPersistence(2L, 20L, 1, 15.0)
        );
        var orden = Orden.fromPersistence(
                1001L, 2002L, "CL 1 # 2-3", new ArrayList<>(items),
                EstadoOrden.deTipo(EstadoOrden.Tipo.PENDIENTE_VALIDACION), null
        );

        OrdenEntity e = OrdenMapper.toEntity(orden);

        assertEquals(1001L, e.getId());
        assertEquals(2002L, e.getClienteId());
        assertEquals("CL 1 # 2-3", e.getDireccionEnviar());
        assertEquals(orden.getEstado().toString(), e.getEstado());
        assertNull(e.getError()); // según el estado de la orden creada
        assertNotNull(e.getItems());
        assertEquals(2, e.getItems().size());
        assertEquals(10L, e.getItems().get(0).getProductoId());
        assertEquals(20L, e.getItems().get(1).getProductoId());
    }
}
