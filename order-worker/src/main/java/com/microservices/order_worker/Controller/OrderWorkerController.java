package com.microservices.order_worker.controller;

import com.microservices.order_worker.dto.OrderRequest;
import com.microservices.order_worker.service.OrderWorkerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/orders")
public class OrderWorkerController {

    private final OrderWorkerService orderWorkerService;

    public OrderWorkerController(OrderWorkerService orderWorkerService) {
        this.orderWorkerService = orderWorkerService;
    }

    @PostMapping
    public String processOrder(@RequestBody OrderRequest request) {
        return orderWorkerService.processOrder(request);
    }
}