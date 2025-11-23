package com.example.events.controller;

import com.example.events.service.OrderService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * POST /api/orders
     * Creates order and triggers events
     */
    @PostMapping
    public Map<String, String> createOrder(@RequestBody OrderRequest request) {
        String result = orderService.createOrder(
                request.orderId(),
                request.email(),
                request.amount()
        );

        return Map.of(
                "status", "success",
                "message", result,
                "note", "Check console for event processing"
        );
    }

    public record OrderRequest(Long orderId, String email, Double amount) {}
}
