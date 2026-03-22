package com.example.order.adapters.in.web;

import com.example.order.adapters.in.web.dto.CreateOrderRequest;
import com.example.order.adapters.in.web.dto.CreateOrderResponse;
import com.example.order.adapters.in.web.dto.GetOrderResponse;
import com.example.order.application.command.CreateOrderCommand;
import com.example.order.application.port.in.CreateOrderUseCase;
import com.example.order.application.port.in.GetOrderUseCase;
import com.example.order.application.result.CreateOrderResult;
import com.example.order.application.result.GetOrderResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, GetOrderUseCase getOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = new CreateOrderCommand(request.product(), request.quantity());
        CreateOrderResult result = createOrderUseCase.execute(command);
        CreateOrderResponse response = new CreateOrderResponse(result.id(), result.product(), result.quantity());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetOrderResponse> getOrderById(Long orderId) {
        GetOrderResult result = getOrderUseCase.execute(orderId);
        GetOrderResponse response = new GetOrderResponse(result.id(), result.product(), result.quantity());
        return ResponseEntity.ok(response);
    }
}
