package com.example.order.application.port.in;

import com.example.order.application.command.CreateOrderCommand;
import com.example.order.application.result.CreateOrderResult;

public interface CreateOrderUseCase {
    CreateOrderResult execute(CreateOrderCommand command, String correlationId);
}
