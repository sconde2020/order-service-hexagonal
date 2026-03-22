package com.example.order.domain.service;

import com.example.order.domain.exception.OrderValidationException;
import com.example.order.domain.model.Order;

public class OrderDomainService {

    public Order buildNewOrder(String product, int quantity) {
        validateProduct(product);
        validateQuantity(quantity);

        Order order = new Order();
        order.setProduct(product);
        order.setQuantity(quantity);
        return order;
    }

    public void validateOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new OrderValidationException("orderId is invalid");
        }
    }

    private void validateProduct(String product) {
        if (product == null || product.isBlank()) {
            throw new OrderValidationException("product is null or empty");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 1 || quantity > 100) {
            throw new OrderValidationException("quantity is out of range");
        }
    }
}
