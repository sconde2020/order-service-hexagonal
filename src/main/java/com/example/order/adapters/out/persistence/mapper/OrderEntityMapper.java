package com.example.order.adapters.out.persistence.mapper;

import com.example.order.adapters.out.persistence.entity.OrderEntity;
import com.example.order.domain.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderEntityMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(order.getId());
        orderEntity.setProduct(order.getProduct());
        orderEntity.setQuantity(order.getQuantity());
        return orderEntity;
    }

    public Order toDomain(OrderEntity orderEntity) {
        Order order = new Order();
        order.setId(orderEntity.getId());
        order.setProduct(orderEntity.getProduct());
        order.setQuantity(orderEntity.getQuantity());
        return order;
    }

}
