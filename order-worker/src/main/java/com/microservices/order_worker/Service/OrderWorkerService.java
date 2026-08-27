package com.microservices.order_worker.service;

import com.microservices.order_worker.dto.OrderRequest;
import org.springframework.stereotype.Service;

@Service
public class OrderWorkerService {

    public String processOrder(OrderRequest request) {

        return "Order processed by worker for "
                + request.getCustomer()
                + " - "
                + request.getProduct()
                + " x "
                + request.getQuantity();
    }
}