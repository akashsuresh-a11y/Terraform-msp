package com.microservices.order_worker.service;

import com.microservices.order_worker.dto.OrderRequest;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
public class SnsPublisherService {

    private static final String TOPIC_ARN =
            "arn:aws:sns:ap-south-1:527165097816:order-notifications";

    private final SnsClient snsClient;

    public SnsPublisherService() {
        this.snsClient = SnsClient.builder().build();
    }

    public void publishOrder(OrderRequest request) {

        String message =
                "customer=" + request.getCustomer()
                + ",product=" + request.getProduct()
                + ",quantity=" + request.getQuantity();

        PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(TOPIC_ARN)
                .message(message)
                .build();

        snsClient.publish(publishRequest);

        System.out.println(
                "Order notification published to SNS: " + message
        );
    }
}