package com.example.order.application.port.out;

import com.example.order.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order, String  correlationId);
    Optional<Order> findById(Long id,  String  correlationId);
    List<Order> findAll(String correlationId);
}
