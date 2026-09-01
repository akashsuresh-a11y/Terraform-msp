package com.microservices.order_worker.service;

import com.microservices.order_worker.dto.OrderRequest;
import org.springframework.stereotype.Service;

@Service
public class OrderWorkerService {

    private final SnsPublisherService snsPublisherService;

    public OrderWorkerService(SnsPublisherService snsPublisherService) {
        this.snsPublisherService = snsPublisherService;
    }

    public String processOrder(OrderRequest request) {

        String message =
                "Order processed for customer: "
                        + request.getCustomer()
                        + ", Product: "
                        + request.getProduct()
                        + ", Quantity: "
                        + request.getQuantity();

        snsPublisherService.publish(message);

        return message;
    }
}