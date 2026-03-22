package com.example.order.infrastructure.config;

import com.example.order.domain.service.OrderDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainService();
    }
}

