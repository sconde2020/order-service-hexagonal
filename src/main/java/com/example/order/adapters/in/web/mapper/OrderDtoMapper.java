package com.example.order.adapters.in.web.mapper;

import com.example.order.adapters.in.web.dto.CreateOrderRequest;
import com.example.order.adapters.in.web.dto.CreateOrderResponse;
import com.example.order.adapters.in.web.dto.GetOrderListResponse;
import com.example.order.adapters.in.web.dto.GetOrderResponse;
import com.example.order.application.command.CreateOrderCommand;
import com.example.order.application.result.CreateOrderResult;
import com.example.order.application.result.GetOrderListResult;
import com.example.order.application.result.GetOrderResult;
import org.springframework.stereotype.Component;

@Component
public class OrderDtoMapper {

    public GetOrderListResponse toResponse(GetOrderListResult result) {
        return new GetOrderListResponse(result.orderId(), result.product(), result.quantity());
    }

    public GetOrderResponse toResponse(GetOrderResult result) {
        return new GetOrderResponse(result.id(), result.product(), result.quantity());
    }

    public CreateOrderResponse toResponse(CreateOrderResult result) {
        return new CreateOrderResponse(result.id(), result.product(), result.quantity());
    }

    public CreateOrderCommand toCommand(CreateOrderRequest request) {
        return new CreateOrderCommand(request.product(), request.quantity());
    }

}
