package com.microservices.order_api.controller;

import com.microservices.order_api.dto.OrderRequest;
import com.microservices.order_api.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public String createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }
}
