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

    public OrderService(
            OrderRepository orderRepository,
            RedisTemplate<String, String> redisTemplate) {

        this.orderRepository = orderRepository;
        this.redisTemplate = redisTemplate;
    }

    public String createOrder(OrderRequest request) {

        Order order = new Order(
                request.getCustomer(),
                request.getProduct(),
                request.getQuantity()
        );

        Order savedOrder = orderRepository.save(order);

        String redisKey = "order:" + savedOrder.getId();

        String redisValue =
                "customer=" + savedOrder.getCustomer()
                + ",product=" + savedOrder.getProduct()
                + ",quantity=" + savedOrder.getQuantity();

        redisTemplate.opsForValue().set(redisKey, redisValue);

        return "Order created with ID: " + savedOrder.getId();
    }
}