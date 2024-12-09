package com.microservices.apicallsimulator.services;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.microservices.order.models.dto.OrderRequest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class ApiCallService {

    private final String catalogUrl;
    private final String purchaseUrl;
    private final RestTemplate restTemplate;
    private final ScheduledExecutorService scheduler;
    private final Random random;


    public ApiCallService(String catalogUrl, String purchaseUrl) {
        this.catalogUrl = catalogUrl;
        this.purchaseUrl = purchaseUrl;
        this.restTemplate = new RestTemplate();
        this.scheduler = Executors.newScheduledThreadPool(2);  // 2 threads for parallel calls
        this.random = new Random();
    }


    // Generate a random interval between calls
    private int randomInterval() {
        return 15 + random.nextInt(30); // random interval between 15-30 seconds
    }


    private void simulateCatalogSearch() {
        try {
            String searchUrl = catalogUrl + "/1701"; //ID of a product
            ResponseEntity<String> response = restTemplate.getForEntity(searchUrl, String.class);
            System.out.println("Catalog Search Response: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Error in Catalog Search API Call: " + e.getMessage());
        }
    }

    // Simulate a purchase request to the purchase service
    private void simulatePurchase() {
        try {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setUserId("1234");
            orderRequest.setCount(1);
            orderRequest.setProductId("1701");

            //Convert the OrderRequest to an HttpEntity
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<OrderRequest> requestEntity = new HttpEntity<>(orderRequest, headers);

            //Send the POST request
            ResponseEntity<String> response = restTemplate.exchange(
                    purchaseUrl + "/buy",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            System.out.println("Purchase API Response: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Error in Purchase API Call: " + e.getMessage());
        }
    }



    public void startSimulation(long duration, TimeUnit unit) {
        System.out.println("Starting API call simulation...");

        // Schedule the end of the simulation
        scheduler.schedule(this::stopSimulation, duration, unit);

        // Schedule API calls with random intervals
        scheduler.scheduleAtFixedRate(this::simulateCatalogSearch, 0, randomInterval(), TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::simulatePurchase, 0, randomInterval(), TimeUnit.SECONDS);
    }

    // Method to stop the simulation
    public void stopSimulation() {
        System.out.println("Stopping API call simulation...");
        scheduler.shutdown();
    }

}

