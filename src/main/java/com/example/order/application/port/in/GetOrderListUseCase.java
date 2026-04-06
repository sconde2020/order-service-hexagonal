package com.example.order.application.port.in;

import com.example.order.application.result.GetOrderListResult;

import java.util.List;

public interface GetOrderListUseCase {
    List<GetOrderListResult> execute(String correlationId);
}
