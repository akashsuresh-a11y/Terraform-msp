package com.microservices.order_api.service;

import com.microservices.order_api.dto.OrderRequest;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public String createOrder(OrderRequest request) {

        return "Order created for "
                + request.getCustomer()
                + " - "
                + request.getProduct()
                + " x "
                + request.getQuantity();
    }
}