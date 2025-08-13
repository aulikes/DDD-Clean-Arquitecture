package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.ProductoValidadoEvent;
import com.aug.ecommerce.application.services.OrdenValidacionService;
import com.aug.ecommerce.application.services.ValidacionCrearOrden;
import com.aug.ecommerce.infrastructure.messaging.IntegrationEventWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenKafkaListenerProductoValidadoTest {

    @Mock private OrdenValidacionService ordenValidacionService;
    @Mock private ObjectMapper objectMapper;
    @InjectMocks private OrdenKafkaListener listener;

    private ProductoValidadoEvent prodOk;
    private ConsumerRecord<String, IntegrationEventWrapper<ProductoValidadoEvent>> record;

    @BeforeEach
    void setUp() {
        prodOk = new ProductoValidadoEvent(12L);
        record = new ConsumerRecord<>("producto.validado", 0, 0L, null,
                IntegrationEventWrapper.wrap(prodOk, prodOk.getEventType(), prodOk.getVersion(), "t3", Instant.now()));
    }

    @Test
    void invocaServicioEnExito() {
        when(objectMapper.convertValue(any(), eq(ProductoValidadoEvent.class))).thenReturn(prodOk);

        listener.productoValidado(record);

        verify(ordenValidacionService).registrarValidacionExitosa(12L, ValidacionCrearOrden.PRODUCTO);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    void noInvocaServicioSiFallaMapeo() {
        when(objectMapper.convertValue(any(), eq(ProductoValidadoEvent.class))).thenThrow(new RuntimeException("mapper"));

        assertDoesNotThrow(() -> listener.productoValidado(record));
        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    void manejaPayloadNulo() {
        var recNull = new ConsumerRecord<String, IntegrationEventWrapper<ProductoValidadoEvent>>(
                "producto.validado", 0, 1L, null, null);

        assertDoesNotThrow(() -> listener.productoValidado(recNull));
        verifyNoInteractions(ordenValidacionService);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void capturaExcepcionDelServicio() {
        when(objectMapper.convertValue(any(), eq(ProductoValidadoEvent.class))).thenReturn(prodOk);
        doThrow(new RuntimeException("svc")).when(ordenValidacionService)
                .registrarValidacionExitosa(12L, ValidacionCrearOrden.PRODUCTO);

        assertDoesNotThrow(() -> listener.productoValidado(record));
        verify(ordenValidacionService).registrarValidacionExitosa(12L, ValidacionCrearOrden.PRODUCTO);
    }
}
