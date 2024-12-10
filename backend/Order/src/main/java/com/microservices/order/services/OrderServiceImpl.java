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

@Service // Indica che questa classe è un servizio gestito da Spring
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository; // Repository per la gestione degli ordini
    private final RestTemplate restTemplate; // RestTemplate per le chiamate HTTP
    private final FeignProductService feignProductService; // Client Feign per comunicare con il servizio "catalog"
    private final CircuitBreakerFactory circuitBreakerFactory; // Gestione del Circuit Breaker

    // Costruttore con iniezione delle dipendenze
    public OrderServiceImpl(OrderRepository orderRepository,
                            RestTemplate restTemplate,
                            FeignProductService feignProductService,
                            CircuitBreakerFactory circuitBreakerFactory) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.feignProductService = feignProductService;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    // Restituisce gli acquisti di un utente specifico
    @Override
    public List<Order> getUserPurchases(String userId) {
        return orderRepository.findByUserId(userId);
    }

    // Restituisce i dettagli di un acquisto specifico per userId e id ordine
    @Override
    public Optional<Order> getPurchase(String userId, String id) {
        return orderRepository.findByUserIdAndId(userId, id);
    }

    // Metodo per effettuare un acquisto
    @Override
    public Optional<Order> buy(String userId, Integer count, String productId) {

        // Recupera il prodotto dal servizio "catalog" tramite Feign
        Optional<Product> product = Optional.ofNullable(feignProductService.getProduct(productId));

        // Verifica la disponibilità del prodotto
        return product.map(p -> {
            if (p.getAvailability() >= count) {
                // Crea un nuovo ordine
                Order newOrder = new Order();
                newOrder.setUserId(userId);
                newOrder.setProductId(productId);
                newOrder.setProductTitle(p.getTitle());
                newOrder.setProductCategory(p.getCategory());
                newOrder.setPrice(p.getPrice() * count); // Calcola il prezzo totale
                newOrder.setQuantity(count);

                // Salva l'ordine nel database
                Order order = orderRepository.save(newOrder);

                // Aggiorna la disponibilità del prodotto
                feignProductService.updateProductAvailability(
                        productId,
                        p.getAvailability() - count);

                // Restituisce l'ordine appena creato
                return order;
            }
            // Lancia un'eccezione se il prodotto non è disponibile
            throw new RuntimeException("Product not available");
        });
    }
}
