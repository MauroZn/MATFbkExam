package com.microservices.order.controllers;

import com.microservices.order.models.Order;
import com.microservices.order.models.dto.OrderRequest;
import com.microservices.order.services.interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
// Disable when gateway is enabled
//@RequestMapping("/api/purchases")
public class OrderController {

    @Autowired
    DiscoveryClient discoveryClient;

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    //List user purchases : /purchases/{userId}
    @GetMapping("/{userId}")
    public List<Order> getUserPurchases(@PathVariable String userId) {
        return orderService.getUserPurchases(userId);
    }

    //Get purchase: /purchases/{userId}/{id}
    @GetMapping("/{userId}/{id}")
    public Optional<Order> getPurchase(@PathVariable String userId,
                                       @PathVariable String id) {
        return orderService.getPurchase(userId, id);
    }

    //Buy: POST /purchases/buy
    @PostMapping("/buy")
    public Optional<Order> buy(@RequestBody OrderRequest orderRequest) {
        return orderService.buy(
                orderRequest.getUserId(),
                orderRequest.getCount(),
                orderRequest.getProductId());
    }

    @GetMapping("/test")
    public String test() {
        discoveryClient.getInstances("catalog").forEach(i -> System.out.println(i.getClass()));
        return "test";
    }
}
