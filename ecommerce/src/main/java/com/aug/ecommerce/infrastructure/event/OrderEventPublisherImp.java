package com.aug.ecommerce.infrastructure.event;

import com.aug.ecommerce.application.event.OrdenCreadaEvent;
import com.aug.ecommerce.application.event.OrderPaymentRequestedEvent;
import com.aug.ecommerce.application.publisher.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisherImp implements OrderEventPublisher {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publishOrderOrdenCreated(OrdenCreadaEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishOrderPaymentRequested(OrderPaymentRequestedEvent event) {
        publisher.publishEvent(event);
    }
}
