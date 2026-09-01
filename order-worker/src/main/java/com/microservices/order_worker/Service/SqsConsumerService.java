package com.microservices.order_worker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Service
public class SqsConsumerService {

    private static final String QUEUE_URL =
            "https://sqs.ap-south-1.amazonaws.com/527165097816/order-notifications-queue";

    private final SqsClient sqsClient;
    private final SesEmailService sesEmailService;
    private final ObjectMapper objectMapper;

    public SqsConsumerService(SesEmailService sesEmailService) {

        this.sesEmailService = sesEmailService;
        this.objectMapper = new ObjectMapper();

        this.sqsClient = SqsClient.builder()
                .region(Region.AP_SOUTH_1)
                .build();
    }

    @PostConstruct
    public void startPolling() {

        Thread pollingThread = new Thread(() -> {

            System.out.println("SQS consumer started");

            while (true) {

                try {
                    ReceiveMessageRequest receiveRequest =
                            ReceiveMessageRequest.builder()
                                    .queueUrl(QUEUE_URL)
                                    .maxNumberOfMessages(10)
                                    .waitTimeSeconds(20)
                                    .build();

                    List<Message> messages =
                            sqsClient.receiveMessage(receiveRequest)
                                    .messages();

                    for (Message message : messages) {

                        System.out.println(
                                "Received message from SQS"
                        );

                        try {
                            JsonNode snsEnvelope =
                                    objectMapper.readTree(message.body());

                            JsonNode messageNode =
                                    snsEnvelope.get("Message");

                            if (messageNode == null
                                    || messageNode.isNull()) {

                                throw new RuntimeException(
                                        "SNS message does not contain a Message field"
                                );
                            }

                            String orderDetails =
                                    messageNode.asText();

                            System.out.println(
                                    "Order details extracted from SNS: "
                                            + orderDetails
                            );

                            // Send ONLY the actual order message to SES.
                            sesEmailService.sendEmail(orderDetails);

                            // Delete the SQS message only after
                            // the email has been sent successfully.
                            sqsClient.deleteMessage(
                                    DeleteMessageRequest.builder()
                                            .queueUrl(QUEUE_URL)
                                            .receiptHandle(
                                                    message.receiptHandle()
                                            )
                                            .build()
                            );

                            System.out.println(
                                    "Email sent successfully. "
                                            + "Message deleted from SQS."
                            );

                        } catch (Exception e) {

                            System.err.println(
                                    "Failed to process SQS message: "
                                            + e.getMessage()
                            );

                            /*
                             * Do not delete the message if processing
                             * or SES fails. It can be retried after
                             * the SQS visibility timeout.
                             */
                        }
                    }

                } catch (Exception e) {

                    System.err.println(
                            "Error polling SQS: "
                                    + e.getMessage()
                    );

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

        });

        pollingThread.setDaemon(true);
        pollingThread.start();
    }
}