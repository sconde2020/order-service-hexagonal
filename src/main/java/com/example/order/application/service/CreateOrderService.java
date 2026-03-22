package com.example.order.application.service;

import com.example.order.application.command.CreateOrderCommand;
import com.example.order.application.port.in.CreateOrderUseCase;
import com.example.order.application.port.out.OrderRepositoryPort;
import com.example.order.application.result.CreateOrderResult;
import com.example.order.domain.model.Order;
import com.example.order.domain.service.OrderDomainService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderService implements CreateOrderUseCase {

     private final OrderRepositoryPort orderRepositoryPort;
     private final OrderDomainService orderDomainService;

     public CreateOrderService(
             OrderRepositoryPort orderRepositoryPort,
             OrderDomainService orderDomainService
     ) {
          this.orderRepositoryPort = orderRepositoryPort;
          this.orderDomainService = orderDomainService;
     }

     @Override
     @Transactional
     public CreateOrderResult execute(CreateOrderCommand command) {
       Order order = orderDomainService.buildNewOrder(command.product(), command.quantity());

       Order savedOrder = orderRepositoryPort.save(order);

       return new CreateOrderResult(savedOrder.getId(), savedOrder.getProduct(), savedOrder.getQuantity());
     }
}
