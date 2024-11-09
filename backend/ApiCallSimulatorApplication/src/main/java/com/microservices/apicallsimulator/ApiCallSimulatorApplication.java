package com.microservices.apicallsimulator;

import com.microservices.apicallsimulator.services.ApiCallService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiCallSimulatorApplication {

    public static void main(String[] args) {

        String catalogUrl = "http://localhost:9000/products";

        String purchaseUrl = "http://localhost:9000/purchases";

        ApiCallService simulator = new ApiCallService(catalogUrl, purchaseUrl);
        simulator.startSimulation(2, TimeUnit.HOURS);
    }
}