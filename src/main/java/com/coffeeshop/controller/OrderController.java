package com.coffeeshop.controller;

import com.coffeeshop.dto.order.OrderRequest;
import com.coffeeshop.dto.order.OrderResponse;
import com.coffeeshop.entity.Order;
import com.coffeeshop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public ResponseEntity<OrderResponse> createOrder
            (@Valid @RequestBody OrderRequest request) {
        Order order = orderService.createOrder(
                request.userId(),
                request.description(),
                request.status()
        );

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getDescription(),
                order.getStatus(),
                order.getCreatedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getDescription(),
                order.getStatus(),
                order.getCreatedAt()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId
            (@PathVariable Long userId) {

        List<Order> orders = orderService.getOrdersByUserId(userId);

        List<OrderResponse> orderResponses = orders.stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getUser().getId(),
                        order.getDescription(),
                        order.getStatus(),
                        order.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/user/{userId}/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatusAndUserId
            (@PathVariable Long userId,
             @PathVariable String status) {

        List<Order> orders = orderService.getOrdersByStatusAndUserId(status, userId);

        List<OrderResponse> orderResponses = orders.stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getUser().getId(),
                        order.getDescription(),
                        order.getStatus(),
                        order.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(orderResponses);
    }
}
