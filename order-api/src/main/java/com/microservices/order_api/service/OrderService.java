package com.microservices.order_api.service;

import com.microservices.order_api.dto.OrderRequest;
import com.microservices.order_api.model.Order;
import com.microservices.order_api.repository.OrderRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final OrderWorkerClient orderWorkerClient;

    public OrderService(
            OrderRepository orderRepository,
            RedisTemplate<String, String> redisTemplate,
            OrderWorkerClient orderWorkerClient) {

        this.orderRepository = orderRepository;
        this.redisTemplate = redisTemplate;
        this.orderWorkerClient = orderWorkerClient;
    }

    public String createOrder(OrderRequest request) {

        // Save order in RDS
        Order order = new Order(
                request.getCustomer(),
                request.getProduct(),
                request.getQuantity()
        );

        Order savedOrder = orderRepository.save(order);

        // Cache order in Valkey
        String redisKey = "order:" + savedOrder.getId();

        String redisValue =
                "customer=" + savedOrder.getCustomer()
                + ",product=" + savedOrder.getProduct()
                + ",quantity=" + savedOrder.getQuantity();

        redisTemplate.opsForValue().set(redisKey, redisValue);

        // Send order to Order Worker through Service Discovery
        String workerResponse = orderWorkerClient.processOrder(request);

        return "Order created with ID: "
                + savedOrder.getId()
                + " | "
                + workerResponse;
    }
}