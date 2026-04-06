package com.example.order.application.service;

import com.example.order.application.command.CreateOrderCommand;
import com.example.order.application.port.in.CreateOrderUseCase;
import com.example.order.application.port.out.OrderEventPublisherPort;
import com.example.order.application.port.out.OrderRepositoryPort;
import com.example.order.application.result.CreateOrderResult;
import com.example.order.domain.model.Order;
import com.example.order.domain.service.OrderDomainService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderDomainService orderDomainService;
    private final OrderRepositoryPort orderRepository;
     private final OrderEventPublisherPort eventPublisher;

     public CreateOrderService(
             OrderDomainService orderDomainService,
             OrderRepositoryPort orderRepository,
             OrderEventPublisherPort eventPublisher
     ) {
         this.orderDomainService = orderDomainService;
         this.orderRepository = orderRepository;
         this.eventPublisher = eventPublisher;
     }

     @Override
     @Transactional
     public CreateOrderResult execute(CreateOrderCommand command, String correlationId) {
       Order order = orderDomainService.buildNewOrder(command.product(), command.quantity());

       Order savedOrder = orderRepository.save(order, correlationId);

       eventPublisher.publishOrderCreated(savedOrder, correlationId);

       return new CreateOrderResult(savedOrder.getId(), savedOrder.getProduct(), savedOrder.getQuantity());
     }
}
