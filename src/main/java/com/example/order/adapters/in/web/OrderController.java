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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
    @Parameter(
            name = "correlationId",
            description = "Unique identifier for tracing the request across services",
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestHeader String correlationId,
            @RequestBody CreateOrderRequest request
    ) {
        CreateOrderCommand command = dtoMapper.toCommand(request);
        CreateOrderResult result = createOrderUseCase.execute(command, correlationId);
        CreateOrderResponse response = dtoMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Parameter(
            name = "correlationId",
            description = "Unique identifier for tracing the request across services",
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    @Parameter(
            name = "id",
            in = ParameterIn.PATH,
            description = "Unique identifier of the order to retrieve",
            required = true,
            example = "1"
    )
    public ResponseEntity<GetOrderResponse> getOrderById(
            @RequestHeader String correlationId,
            @PathVariable("id") Long orderId
    ) {
        GetOrderResult result = getOrderUseCase.execute(orderId, correlationId);
        GetOrderResponse response = dtoMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Parameter(
            name = "correlationId",
            description = "Unique identifier for tracing the request across services",
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    public ResponseEntity<List<GetOrderListResponse>> getAllOrders(
            @RequestHeader String correlationId
    ) {
        List<GetOrderListResponse> response =
                getOrderListUseCase.execute(correlationId).stream()
                .map(dtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
