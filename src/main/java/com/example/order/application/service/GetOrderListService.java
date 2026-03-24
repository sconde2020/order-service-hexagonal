package com.example.order.application.service;

import com.example.order.application.port.in.GetOrderListUseCase;
import com.example.order.application.port.out.OrderRepositoryPort;
import com.example.order.application.result.GetOrderListResult;
import com.example.order.domain.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetOrderListService implements GetOrderListUseCase {

    private final OrderRepositoryPort orderRepository;

    public GetOrderListService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<GetOrderListResult> execute() {
        return this.orderRepository.findAll().stream()
                .map(this::mapToResult).toList();
    }

    private GetOrderListResult mapToResult(Order order) {
        return new GetOrderListResult(order.getId(), order.getProduct(), order.getQuantity());
    }
}