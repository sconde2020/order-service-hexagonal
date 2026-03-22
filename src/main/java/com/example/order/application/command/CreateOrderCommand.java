package com.example.order.application.command;

public record CreateOrderCommand(String product, int quantity) {
}
