package com.microservices.order.services.interfaces;

import com.microservices.order.models.Order;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    //List user purchases : /purchases/{userId}
    List<Order> getUserPurchases(String userId);

    //Get purchase: /purchases/{userId}/{id}
    Optional<Order> getPurchase(String userId, String id);

    //Buy: POST /purchases
    Optional<Order> buy(String userId, Integer count, String productId);
}
