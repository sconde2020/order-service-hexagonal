package com.example.order.application.port.in;

import com.example.order.application.result.GetOrderResult;

public interface GetOrderUseCase {
    GetOrderResult execute(Long orderId,  String correlationId);
}
