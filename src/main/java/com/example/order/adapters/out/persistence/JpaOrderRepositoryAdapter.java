package com.example.order.adapters.out.persistence;

import com.example.order.application.port.out.OrderRepositoryPort;
import com.example.order.domain.model.Order;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository repository;

    public JpaOrderRepositoryAdapter(SpringDataOrderRepository repository) {
        this.repository = repository;
    }


    @Override
    public Order save(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setProduct(order.getProduct());
        entity.setQuantity(order.getQuantity());

        OrderEntity savedEntity = repository.save(entity);

        return new Order(
                savedEntity.getId(),
                savedEntity.getProduct(),
                savedEntity.getQuantity()
        );
    }

    @Override
    public Optional<Order> findById(Long id) {
        return repository.findById(id)
                .map(entity -> new Order(
                        entity.getId(),
                        entity.getProduct(),
                        entity.getQuantity())
                );
    }
}
