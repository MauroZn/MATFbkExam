package com.microservices.apicallsimulator;

import com.microservices.apicallsimulator.services.ApiCallService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.TimeUnit;


@SpringBootApplication
@EnableDiscoveryClient
public class ApiCallSimulatorApplication {

    @Value("${catalog.url}")
    static String catalogUrl;

    @Value("${purchase.url}")
    static String purchaseUrl;

    public static void main(String[] args) {
        ApiCallService simulator = new ApiCallService(catalogUrl, purchaseUrl);
        simulator.startSimulation(2, TimeUnit.HOURS);
    }
}