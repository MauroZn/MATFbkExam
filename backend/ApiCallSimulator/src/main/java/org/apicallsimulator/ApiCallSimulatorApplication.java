package org.apicallsimulator;

import org.apicallsimulator.simulator.ApiCallSimulator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiCallSimulatorApplication {

    public static void main(String[] args) {

        String catalogUrl = "http://localhost:9000/products";

        String purchaseUrl = "http://localhost:9000/purchases";

        ApiCallSimulator simulator = new ApiCallSimulator(catalogUrl, purchaseUrl);
        simulator.startSimulation(2, TimeUnit.HOURS);
    }
}