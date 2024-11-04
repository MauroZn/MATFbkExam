package com.microservices.order.services;

import com.microservices.order.models.Order;
import com.microservices.order.models.shared.Product;
import com.microservices.order.repositories.OrderRepository;
import com.microservices.order.services.interfaces.OrderService;
import com.microservices.order.services.openfeign.FeignProductService;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final FeignProductService feignProductService;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public OrderServiceImpl(OrderRepository orderRepository,
                            RestTemplate restTemplate,
                            FeignProductService feignProductService,
                            CircuitBreakerFactory circuitBreakerFactory) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.feignProductService = feignProductService;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public List<Order> getUserPurchases(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Optional<Order> getPurchase(String userId, String id) {
        return orderRepository.findByUserIdAndId(userId, id);
    }

    @Override
    public Optional<Order> buy(String userId, Integer count, String productId) {

        /*CircuitBreaker circuitBreaker = circuitBreakerFactory
                .create("product-service-circuit-breaker");*/

//        RestTemplate way
//
        /*Optional<Product> product =
                Optional.ofNullable(restTemplate.getForObject(
                        "http://catalog/api/products/{productId}",
                        Product.class,
                        productId));*/

//      Feign Way + CircuitBreaker

        Optional<Product> product = Optional.ofNullable(feignProductService.getProduct(productId));

        /*Optional<Product> product =
                Optional.ofNullable(
                        circuitBreaker.run(
                                () -> {
                                    // Call catalog service
                                    return feignProductService
                                            .getProduct(productId);
                                },
                                throwable -> {
                                    // return something
                                    throw new RuntimeException("Product Service Not available");
                                }
                        )
                );*/

        // 1. check disponibilita'
        return product.map(p -> {
            if (p.getAvailability() >= count) {
                Order newOrder = new Order();
                newOrder.setUserId(userId);
                newOrder.setProductId(productId);
                newOrder.setProductTitle(p.getTitle());
                newOrder.setProductCategory(p.getCategory());
                newOrder.setPrice(p.getPrice() * count);
                newOrder.setQuantity(count);

                // 2. Save the order
                Order order = orderRepository.save(newOrder);

                // 3. Update product availability
                // RestTemplate way
                /*restTemplate.exchange(
                        "http://catalog/api/products/{productId}/availability/{quantity}",
                        HttpMethod.PUT,
                        null,
                        Product.class,
                        productId,
                        p.getAvailability() - count).getBody();*/


                feignProductService.updateProductAvailability(
                        productId,
                        p.getAvailability() - count);

                // 4. return the order
                return order;
            }
            throw new RuntimeException("Product not available");
        });
    }
}
