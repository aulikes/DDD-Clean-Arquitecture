package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.commands.CrearInventarioCommand;
import com.aug.ecommerce.application.events.OrdenCreadaEvent;
import com.aug.ecommerce.application.events.ProductoCreadoEvent;
import com.aug.ecommerce.application.services.InventarioService;
import com.aug.ecommerce.application.services.InventarioValidacionService;
import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para InventarioKafkaListener.
 * Se cubren ambos métodos del listener y escenarios de éxito, fallas en mapeo,
 * payloads nulos y excepciones internas del servicio.
 */
@ExtendWith(MockitoExtension.class)
class InventarioKafkaListenerTest {

    @Mock
    private InventarioService inventarioService;

    @Mock
    private InventarioValidacionService inventarioValidacionService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private InventarioKafkaListener listener;

    private OrdenCreadaEvent ordenEvent;
    private ConsumerRecord<String, IntegrationEventWrapper<OrdenCreadaEvent>> ordenRecord;

    private ProductoCreadoEvent productoEvent;
    private ConsumerRecord<String, IntegrationEventWrapper<ProductoCreadoEvent>> productoRecord;

    @BeforeEach
    void setUp() {
        // Se crea un evento de orden con ítems representativos para validar inventario.
        ordenEvent = new OrdenCreadaEvent(
                1001L,
                2002L,
                "Av. Siempre Viva 123",
                List.of(new OrdenCreadaEvent.ItemOrdenCreada(10L, 3),
                        new OrdenCreadaEvent.ItemOrdenCreada(20L, 1))
        );
        IntegrationEventWrapper<OrdenCreadaEvent> ordenWrapper = IntegrationEventWrapper.wrap(
                ordenEvent, ordenEvent.getEventType(), ordenEvent.getVersion(), "trace-x", Instant.now()
        );
        ordenRecord = new ConsumerRecord<>("orden.multicast.creada", 0, 0L, null, ordenWrapper);

        // Se crea un evento de producto para creación de inventario.
        productoEvent = new ProductoCreadoEvent(555L, 99L);
        IntegrationEventWrapper<ProductoCreadoEvent> productoWrapper = IntegrationEventWrapper.wrap(
                productoEvent, productoEvent.getEventType(), productoEvent.getVersion(), "trace-y", Instant.now()
        );
        productoRecord = new ConsumerRecord<>("producto.creado", 0, 0L, null, productoWrapper);
    }

    // --------- onMessageOrdenCreada ---------

    @Test
    @DisplayName("onMessageOrdenCreada: Invoca validarInventarioCreacionOrden con datos correctos")
    void ordenCreada_shouldValidateInventory_whenPayloadIsValid() throws Exception {
        // Se simula que el mapper reconstruye correctamente el evento.
        when(objectMapper.convertValue(any(), eq(OrdenCreadaEvent.class))).thenReturn(ordenEvent);

        // Ejecución del listener.
        listener.onMessageOrdenCreada(ordenRecord);

        // Verificación: el servicio de validación debe recibir los parámetros exactos.
        verify(inventarioValidacionService, times(1))
                .validarInventarioCreacionOrden(eq(1001L), eq(ordenEvent.items()));
        verifyNoMoreInteractions(inventarioValidacionService);
        verifyNoInteractions(inventarioService);
    }

    @Test
    @DisplayName("onMessageOrdenCreada: No invoca servicio cuando falla el mapeo del payload")
    void ordenCreada_shouldNotCallService_whenMappingFails() {
        // Se fuerza un fallo en la conversión del payload.
        when(objectMapper.convertValue(any(), eq(OrdenCreadaEvent.class)))
                .thenThrow(new RuntimeException("mapper error"));

        listener.onMessageOrdenCreada(ordenRecord);

        // Verificación: no se debe invocar ningún servicio.
        verifyNoInteractions(inventarioValidacionService);
        verifyNoInteractions(inventarioService);
    }

    @Test
    @DisplayName("onMessageOrdenCreada: Maneja valor nulo en el record sin invocar servicio")
    void ordenCreada_shouldHandleNullRecordValue() {
        // Se simula un record con valor nulo.
        ConsumerRecord<String, IntegrationEventWrapper<OrdenCreadaEvent>> nullRecord =
                new ConsumerRecord<>("orden.multicast.creada", 0, 1L, null, null);

        listener.onMessageOrdenCreada(nullRecord);

        // Verificación: no hay interacciones con servicios ni con el mapper.
        verifyNoInteractions(inventarioValidacionService);
        verifyNoInteractions(inventarioService);
        verifyNoInteractions(objectMapper);
    }

    @Test
    @DisplayName("onMessageOrdenCreada: Captura excepción del servicio sin propagarla")
    void ordenCreada_shouldCatchServiceException() throws Exception {
        when(objectMapper.convertValue(any(), eq(OrdenCreadaEvent.class))).thenReturn(ordenEvent);
        doThrow(new RuntimeException("service error"))
                .when(inventarioValidacionService).validarInventarioCreacionOrden(eq(1001L), eq(ordenEvent.items()));

        listener.onMessageOrdenCreada(ordenRecord);

        // Verificación: hubo intento de validación exactamente una vez.
        verify(inventarioValidacionService, times(1))
                .validarInventarioCreacionOrden(eq(1001L), eq(ordenEvent.items()));
    }

    // --------- onMessageProductoCreado ---------

    @Test
    @DisplayName("onMessageProductoCreado: Invoca crearInvenario con command correcto")
    void productoCreado_shouldCreateInventory_whenPayloadIsValid() {
        when(objectMapper.convertValue(any(), eq(ProductoCreadoEvent.class))).thenReturn(productoEvent);

        listener.onMessageProductoCreado(productoRecord);

        // Se captura el comando para validar sus campos.
        ArgumentCaptor<CrearInventarioCommand> captor = ArgumentCaptor.forClass(CrearInventarioCommand.class);
        verify(inventarioService, times(1)).crearInvenario(captor.capture());
        CrearInventarioCommand cmd = captor.getValue();
        assertEquals(555L, cmd.productoId());
        assertEquals(99L, cmd.cantidad());
        verifyNoMoreInteractions(inventarioService);
        verifyNoInteractions(inventarioValidacionService);
    }

    @Test
    @DisplayName("onMessageProductoCreado: No invoca servicio cuando falla el mapeo del payload")
    void productoCreado_shouldNotCallService_whenMappingFails() {
        when(objectMapper.convertValue(any(), eq(ProductoCreadoEvent.class)))
                .thenThrow(new RuntimeException("mapper error"));

        listener.onMessageProductoCreado(productoRecord);

        verifyNoInteractions(inventarioService);
        verifyNoInteractions(inventarioValidacionService);
    }

    @Test
    @DisplayName("onMessageProductoCreado: Maneja valor nulo en el record sin invocar servicio")
    void productoCreado_shouldHandleNullRecordValue() {
        ConsumerRecord<String, IntegrationEventWrapper<ProductoCreadoEvent>> nullRecord =
                new ConsumerRecord<>("producto.creado", 0, 1L, null, null);

        listener.onMessageProductoCreado(nullRecord);

        verifyNoInteractions(inventarioService);
        verifyNoInteractions(inventarioValidacionService);
        verifyNoInteractions(objectMapper);
    }

    @Test
    @DisplayName("onMessageProductoCreado: Captura excepción del servicio sin propagarla")
    void productoCreado_shouldCatchServiceException() {
        when(objectMapper.convertValue(any(), eq(ProductoCreadoEvent.class))).thenReturn(productoEvent);
        doThrow(new RuntimeException("service error"))
                .when(inventarioService).crearInvenario(any(CrearInventarioCommand.class));

        listener.onMessageProductoCreado(productoRecord);

        verify(inventarioService, times(1)).crearInvenario(any(CrearInventarioCommand.class));
    }
}
