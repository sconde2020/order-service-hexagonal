package com.example.order.adapters.in.web;

import com.example.order.adapters.in.web.dto.CreateOrderRequest;
import com.example.order.adapters.in.web.dto.CreateOrderResponse;
import com.example.order.adapters.in.web.dto.GetOrderListResponse;
import com.example.order.adapters.in.web.dto.GetOrderResponse;
import com.example.order.adapters.in.web.mapper.OrderDtoMapper;
import com.example.order.application.command.CreateOrderCommand;
import com.example.order.application.port.in.CreateOrderUseCase;
import com.example.order.application.port.in.GetOrderListUseCase;
import com.example.order.application.port.in.GetOrderUseCase;
import com.example.order.application.result.CreateOrderResult;
import com.example.order.application.result.GetOrderResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetOrderListUseCase getOrderListUseCase;
    private final OrderDtoMapper dtoMapper;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                           GetOrderUseCase getOrderUseCase,
                           GetOrderListUseCase getOrderListUseCase,
                           OrderDtoMapper dtoMapper) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.getOrderListUseCase = getOrderListUseCase;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = dtoMapper.toCommand(request);
        CreateOrderResult result = createOrderUseCase.execute(command);
        CreateOrderResponse response = dtoMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetOrderResponse> getOrderById(Long orderId) {
        GetOrderResult result = getOrderUseCase.execute(orderId);
        GetOrderResponse response = dtoMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GetOrderListResponse>> getAllOrders() {
        List<GetOrderListResponse> response =
                getOrderListUseCase.execute().stream()
                .map(dtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
