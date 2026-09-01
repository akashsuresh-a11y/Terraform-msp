package com.microservices.order_worker.service;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
public class SnsPublisherService {

    private final SnsClient snsClient;

    private static final String TOPIC_ARN =
            "arn:aws:sns:ap-south-1:527165097816:order-notifications";

    public SnsPublisherService() {
        this.snsClient = SnsClient.builder()
                .region(software.amazon.awssdk.regions.Region.AP_SOUTH_1)
                .build();
    }

    public void publish(String message) {

        PublishRequest request = PublishRequest.builder()
                .topicArn(TOPIC_ARN)
                .message(message)
                .build();

        snsClient.publish(request);

        System.out.println(
                "Order notification published to SNS: " + message
        );
    }
}