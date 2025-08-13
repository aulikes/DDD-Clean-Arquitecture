package com.aug.ecommerce.infrastructure.messaging.listener.kafkalistener;

import com.aug.ecommerce.application.events.ProductoNoValidadoEvent;
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
class OrdenKafkaListenerProductoNoValidadoTest {

    @Mock private OrdenValidacionService ordenValidacionService;
    @Mock private ObjectMapper objectMapper;
    @InjectMocks private OrdenKafkaListener listener;

    private ProductoNoValidadoEvent prodFail;
    private ConsumerRecord<String, IntegrationEventWrapper<ProductoNoValidadoEvent>> record;

    @BeforeEach
    void setUp() {
        prodFail = new ProductoNoValidadoEvent(13L);
        record = new ConsumerRecord<>("producto.no-validado", 0, 0L, null,
                IntegrationEventWrapper.wrap(prodFail, prodFail.getEventType(), prodFail.getVersion(), "t4", Instant.now()));
    }

    @Test
    void invocaServicioEnFallo() {
        when(objectMapper.convertValue(any(), eq(ProductoNoValidadoEvent.class))).thenReturn(prodFail);

        listener.productoNoValidado(record);

        verify(ordenValidacionService).registrarValidacionFallida(13L, ValidacionCrearOrden.PRODUCTO);
        verifyNoMoreInteractions(ordenValidacionService);
    }

    @Test
    void noInvocaServicioSiFallaMapeo() {
        when(objectMapper.convertValue(any(), eq(ProductoNoValidadoEvent.class))).thenThrow(new RuntimeException("mapper"));

        assertDoesNotThrow(() -> listener.productoNoValidado(record));
        verifyNoInteractions(ordenValidacionService);
    }

    @Test
    void manejaPayloadNulo() {
        var recNull = new ConsumerRecord<String, IntegrationEventWrapper<ProductoNoValidadoEvent>>(
                "producto.no-validado", 0, 1L, null, null);

        assertDoesNotThrow(() -> listener.productoNoValidado(recNull));
        verifyNoInteractions(ordenValidacionService);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void capturaExcepcionDelServicio() {
        when(objectMapper.convertValue(any(), eq(ProductoNoValidadoEvent.class))).thenReturn(prodFail);
        doThrow(new RuntimeException("svc")).when(ordenValidacionService)
                .registrarValidacionFallida(13L, ValidacionCrearOrden.PRODUCTO);

        assertDoesNotThrow(() -> listener.productoNoValidado(record));
        verify(ordenValidacionService).registrarValidacionFallida(13L, ValidacionCrearOrden.PRODUCTO);
    }
}
