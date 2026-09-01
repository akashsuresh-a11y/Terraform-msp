package com.microservices.order_worker.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Service
public class SesEmailService {

    private static final String SENDER_EMAIL =
            "sureshakash081@gmail.com";

    private static final String RECIPIENT_EMAIL =
            "sureshakash081@gmail.com";

    private final SesClient sesClient;

    public SesEmailService() {
        this.sesClient = SesClient.builder()
                .region(Region.AP_SOUTH_1)
                .build();
    }

    public void sendEmail(String orderDetails) {

        String emailBody =
                "Thank you for your order!\n\n"
                + "Order Details:\n"
                + orderDetails
                + "\n\n"
                + "Thank you for shopping with us!";

        SendEmailRequest request = SendEmailRequest.builder()
                .source(SENDER_EMAIL)
                .destination(
                        Destination.builder()
                                .toAddresses(RECIPIENT_EMAIL)
                                .build()
                )
                .message(
                        Message.builder()
                                .subject(
                                        Content.builder()
                                                .data("Order Confirmation")
                                                .charset("UTF-8")
                                                .build()
                                )
                                .body(
                                        Body.builder()
                                                .text(
                                                        Content.builder()
                                                                .data(emailBody)
                                                                .charset("UTF-8")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        sesClient.sendEmail(request);

        System.out.println(
                "Order confirmation email sent successfully through SES"
        );
    }
}