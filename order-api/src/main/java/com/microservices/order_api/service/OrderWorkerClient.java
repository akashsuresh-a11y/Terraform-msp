package com.microservices.order_api.service;

import com.microservices.order_api.dto.OrderRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@Service
public class OrderWorkerClient {

    private final RestClient restClient;

    public OrderWorkerClient() {
        this.restClient = RestClient.create();
    }

    public String processOrder(OrderRequest request) {

        try {
            Hashtable<String, String> environment = new Hashtable<>();

            environment.put(
                    "java.naming.factory.initial",
                    "com.sun.jndi.dns.DnsContextFactory"
            );

            InitialDirContext context = new InitialDirContext(environment);

            Attributes attributes = context.getAttributes(
                    "order-worker.terraform-msp.local",
                    new String[]{"SRV"}
            );

            Attribute srvAttribute = attributes.get("SRV");

            if (srvAttribute == null || srvAttribute.size() == 0) {
                throw new RuntimeException(
                        "No Order Worker instances found through service discovery"
                );
            }

            String srvRecord = srvAttribute.get(0).toString();

            String[] parts = srvRecord.split("\\s+");

            int port = Integer.parseInt(parts[2]);

            String host = parts[3];

            if (host.endsWith(".")) {
                host = host.substring(0, host.length() - 1);
            }

            String url = "http://" + host + ":" + port + "/internal/orders";

            System.out.println(
                    "Discovered Order Worker through Cloud Map: " + url
            );

            return restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to communicate with Order Worker through service discovery",
                    e
            );
        }
    }
}