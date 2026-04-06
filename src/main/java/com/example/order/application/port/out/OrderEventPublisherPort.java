package com.example.order.application.port.out;

import com.example.order.domain.model.Order;

public interface OrderEventPublisherPort {
    void publishOrderCreated(Order order, String correlationId);
}
