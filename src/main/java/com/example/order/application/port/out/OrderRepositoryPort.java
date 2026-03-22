package com.example.order.application.port.out;

import com.example.order.domain.model.Order;

import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(Long id);
}
