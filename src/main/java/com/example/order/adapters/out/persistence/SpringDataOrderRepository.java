package com.example.order.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrderRepository
        extends JpaRepository<OrderEntity, Long> {
}
