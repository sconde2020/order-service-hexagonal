package com.example.order.application.service;

import com.example.order.application.exception.OrderNotFoundException;
import com.example.order.application.port.in.GetOrderUseCase;
import com.example.order.application.port.out.OrderRepositoryPort;
import com.example.order.application.result.GetOrderResult;
import org.springframework.stereotype.Service;

@Service
public class GetOrderService implements GetOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public GetOrderService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public GetOrderResult execute(Long orderId,  String correlationId) {
        return orderRepositoryPort
                .findById(orderId, correlationId)
                .map(order -> new GetOrderResult(order.getId(), order.getProduct(), order.getQuantity()))
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
    }

}
