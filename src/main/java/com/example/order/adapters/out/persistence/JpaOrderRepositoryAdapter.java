package com.example.order.adapters.out.persistence;

import com.example.order.adapters.out.persistence.entity.OrderEntity;
import com.example.order.adapters.out.persistence.mapper.OrderEntityMapper;
import com.example.order.application.port.out.OrderRepositoryPort;
import com.example.order.domain.model.Order;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository repository;
    private final OrderEntityMapper entityMapper;

    public JpaOrderRepositoryAdapter(SpringDataOrderRepository repository, OrderEntityMapper entityMapper) {
        this.repository = repository;
        this.entityMapper = entityMapper;
    }


    @Override
    public Order save(Order order,  String  correlationId) {
        OrderEntity entity = entityMapper.toEntity(order);
        OrderEntity savedEntity = repository.save(entity);
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(Long id, String  correlationId) {
        return repository.findById(id)
                .map(entityMapper::toDomain);
    }

    @Override
    public List<Order> findAll(String correlationId) {
        return repository.findAll(Sort.by("product"))
                .stream()
                .map(entityMapper::toDomain)
                .toList();
    }
}
